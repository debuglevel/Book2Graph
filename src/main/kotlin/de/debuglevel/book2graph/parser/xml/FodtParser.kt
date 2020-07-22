package de.debuglevel.book2graph.parser.xml

import de.debuglevel.book2graph.book.*
import de.debuglevel.book2graph.parser.Checker
import de.debuglevel.book2graph.parser.OdtParser
import mu.KotlinLogging
import org.w3c.dom.Node
import java.io.File
import java.nio.file.Files

/**
 * Parses a Flat ODT file with specified styles and creates a book object
 */
object FodtParser : OdtParser() {
    private val logger = KotlinLogging.logger {}

    private const val officeNamespace = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    private const val textNamespace = "urn:oasis:names:tc:opendocument:xmlns:text:1.0"
    private const val styleNamespace = "urn:oasis:names:tc:opendocument:xmlns:style:1.0"

    private var document: XElement? = null

    /**
     * Parses a given Flat ODT file
     *
     * @param file the FODT file
     * @return a book object containing the FODT's information
     */
    override fun parse(file: File): Book {
        logger.debug { "Parsing file '$file'..." }

        loadDocument(file)
        val styles = getStyles()
        val paragraphs = getParagraphs()

        val book = Book()
        var lastChapter = Chapter()
        var currentChapter = Chapter()

        for (paragraph in paragraphs) {
            paragraph.style = getStyle(styles, paragraph)
            val styleType = paragraph.style!!.styleType
            when (styleType) {
                StyleType.Title -> {
                    lastChapter = currentChapter
                    currentChapter = Chapter()
                    book.chapters.add(currentChapter)
                    currentChapter.precedingChapter = lastChapter
                    lastChapter.succeedingChapter = currentChapter
                    currentChapter.title = paragraph.content
                    currentChapter.revisionStatus = getRevisionStatus(paragraph.style!!)
                }
                StyleType.Successor -> currentChapter.succeedingChapterReferences.add(paragraph.content)
                StyleType.Predecessor -> currentChapter.precedingChapterReferences.add(paragraph.content)
                StyleType.Summary -> currentChapter.summary.add(paragraph.content)
                StyleType.Comment -> currentChapter.comment.add(paragraph.content)
                StyleType.Content -> currentChapter.text.add(paragraph.content)
                else -> {
                }
            }
        }

        Checker(book).check()

        logger.debug { "Parsing file '$file' done." }
        return book
    }

    private fun getStyle(styles: List<Style>, paragraph: Paragraph): Style? {
        val styleName = paragraph.styleName
        val style = styles.first { s -> s.name == styleName }

        return when (style.isBaseStyle) {
            true -> style
            false -> style.parentStyle
        }
    }

    override fun getStyles(): List<Style> {
        logger.debug { "Getting styles..." }

        val styles = getAllStyles()
        assignBaseStyleTypes(styles)
        assignAutomaticGeneratedStyles(styles)

        logger.debug { "Got ${styles.size} styles." }
        return styles
    }

    private fun getAllStyles(): List<Style> {
        logger.trace { "Getting all styles..." }

        val userDefinedStyles = getUserDefinedStyles()
        val automaticGeneratedStyles = getAutomaticGeneratedStyles()

        val allStyles = automaticGeneratedStyles.union(userDefinedStyles)

        logger.trace { "Got ${allStyles.size} styles." }
        return allStyles.toList()
    }

    /**
     * Finds and sets the base styles for automatic generated styles.
     */
    private fun assignAutomaticGeneratedStyles(styles: List<Style>) {
        for (style in styles) {
            assignAutomaticGeneratedStyle(style, styles)
        }
    }

    private fun assignBaseStyleTypes(styles: List<Style>) {
        val baseStyles = styles.filter { it.isBaseStyle }
        for (style in baseStyles) {
            style.styleType = getStyleType(style.name)
        }
    }

    /**
     * @param type "styles" or "automatic-styles"
     */
    private fun getXmlStyles(type: String): List<Node> {
        logger.trace { "Getting all styles of type '$type' defined in XML..." }

        val nsOffice = officeNamespace
        val nsStyle = styleNamespace

        val xmlStyles = document!!.descendants(nsOffice, type)

        val xmlStyles2 = mutableListOf<Node>()
        for (xmlStyle in xmlStyles) {
            val styleNodes = xmlStyle.childNodes.toMutableList()
                .filter { it.localName == "style" }
            xmlStyles2.addAll(styleNodes)
        }

        logger.trace { "Got ${xmlStyles2.size} styles of type '$type' defined in XML." }
        return xmlStyles2
    }

    private fun getUserDefinedStyles(): List<Style> {
        logger.trace { "Getting user defined styles..." }

        val xmlUserDefinedStyles = getXmlStyles("styles")
        val userDefinedStyles = xmlUserDefinedStyles.map {
            val styleName =
                it.attributes.getNamedItemNS(styleNamespace, "name").textContent
            Style(styleName, true, null)
        }

        logger.trace { "Got ${userDefinedStyles.size} user defined styles." }
        return userDefinedStyles
    }

    private fun getAutomaticGeneratedStyles(): List<Style> {
        logger.trace { "Getting automatic generated styles..." }

        val xmlAutomaticStyles = getXmlStyles("automatic-styles")
        val automaticGeneratedStyles = xmlAutomaticStyles.map {
            val styleName =
                it.attributes.getNamedItemNS(styleNamespace, "name").textContent
            val parentStyleName = it.attributes.getNamedItem("style:parent-style-name")?.textContent
            Style(styleName, false, parentStyleName)
        }

        logger.trace { "Got ${automaticGeneratedStyles.size} automatic generated styles." }
        return automaticGeneratedStyles
    }

    /**
     * Finds and sets the base style for a automatic generated style.
     */
    private fun assignAutomaticGeneratedStyle(style: Style, styles: List<Style>) {
        // style has no parent style; nothing to do
        if (style.parentStyleName == null) {
            return
        }

        // find the base style with the according name
        val parentStyle = styles.firstOrNull { s ->
            s.isBaseStyle && s.name == style.parentStyleName
        }

        if (parentStyle != null) {
            style.parentStyle = parentStyle
        } else {
            // warn if some inconsistency was found
            logger.warn("ParentStyle '${style.parentStyleName}' used by '${style.name}' not found (or is no base style).")
        }
    }

    override fun loadDocument(file: File) {
        logger.debug { "Loading XML file '$file'..." }

        val fileStream = Files.newInputStream(file.toPath())
        document = XElement.load(fileStream)
    }

    override fun getParagraphs(): List<Paragraph> {
        logger.debug { "Getting paragraphs..." }

        val xmlParagraphs = mutableListOf<Node>()

        val bodies = document!!.descendants(officeNamespace, "body")
        for (body in bodies) {
            val textNodes = body.childNodes.toMutableList()
                .filter { it.localName == "text" }

            for (textNode in textNodes) {
                val pNodes = textNode.childNodes.toMutableList()
                    .filter { it.localName == "p" }
                xmlParagraphs.addAll(pNodes)
            }
        }

        val paragraphs = xmlParagraphs.map {
            val styleName = it.attributes.getNamedItem("text:style-name").textContent
            val content = it.textContent
            Paragraph(content, styleName)
        }

        logger.debug { "Got ${paragraphs.size} paragraphs." }
        return paragraphs.toList()
    }
}


