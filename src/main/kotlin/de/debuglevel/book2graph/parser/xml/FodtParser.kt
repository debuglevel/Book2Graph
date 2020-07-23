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
        logger.trace { "Getting style for paragraph(styleName='${paragraph.styleName}')..." }

        val style = styles.first { it.name == paragraph.styleName }

        val userDefinedStyle = when (style.isUserDefinedStyle) {
            true -> style
            false -> style.userDefinedStyle
        }

        logger.trace { "Got style '${userDefinedStyle.toString()}' for paragraph." }
        return userDefinedStyle
    }

    override fun getStyles(): List<Style> {
        logger.debug { "Getting styles..." }
        val styles = FodtStyleParser(document!!).getStyles()
        logger.debug { "Got ${styles.size} styles." }
        return styles
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


