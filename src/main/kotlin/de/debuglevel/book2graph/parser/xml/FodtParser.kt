package de.debuglevel.book2graph.parser.xml

import de.debuglevel.book2graph.book.*
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

    /**
     * Parses a given Flat ODT file
     *
     * @param file the FODT file
     * @return a book object containing the FODT's information
     */
    override fun parse(file: File): Book {
        logger.debug { "Parsing file '$file'..." }

        val document = loadXML(file)
        val styles = getStyles(document)
        val paragraphs = getParagraphs(document)

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
                    currentChapter.revisionStatus =
                        getRevisionStatus(paragraph.style!!)
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

        checkChaptersErrors(book.chapters)

        logger.debug { "Parsing file '$file' done." }
        return book
    }

    private fun getStyle(styles: List<Style>, paragraph: Paragraph): Style? {
        //Trace.traceInformation("getStyle: "+paragraph.styleName)
        val styleName = paragraph.styleName
        val style = styles.first { s -> s.name == styleName }

        return when {
            style.isBaseStyle -> style
            else -> style.parentStyle
        }
    }

    private fun getStyleType(styleName: String): StyleType {
        return when {
            styleName.startsWith("ZZTitel") -> StyleType.Title
            styleName == "ZZEinordnungDanach" -> StyleType.Successor
            styleName == "ZZEinordnungVorher" -> StyleType.Predecessor
            styleName == "ZZZusammenfassung" -> StyleType.Summary
            styleName == "ZZKommentar" -> StyleType.Comment
            styleName == "ZZInhalt" -> StyleType.Content
            else -> //Trace.traceWarning("unknown style name used: " + paragraph.StyleName);
                //paragraph.DebugInformation.Add(new KeyValuePair<DebugInformationType, object>(DebugInformationType.UnknownStyle, paragraph.StyleName));
                StyleType.Unknown
        }
    }

    private fun getStyles(document: XElement): List<Style> {
        logger.debug { "Getting styles..." }

        val styles = getAllStyles(document)
        assignAutomaticStyles(styles)
        assignBaseStyleTypes(styles)
        return styles
    }

    private fun assignBaseStyleTypes(styles: List<Style>) {
        for (style in styles.filter { s -> s.isBaseStyle }) {
            style.styleType = getStyleType(style.name)
        }
    }

    private fun getAllStyles(document: XElement): List<Style> {
        val nsOffice = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"
        val nsStyle = "urn:oasis:names:tc:opendocument:xmlns:style:1.0"

        val xmlDefinedStyles = mutableListOf<Node>()
        var xmlStyles =
            document.descendants(nsOffice, "styles") //.descendants(nsOffice, "text").descendants(nsText, "p")
        for (xmlStyle in xmlStyles) {
            val styleNodes = XElement.toMutableList(
                xmlStyle.childNodes
            ).filter { x ->
                x.localName == "style"
            }
            xmlDefinedStyles.addAll(styleNodes)

        }
        val definedStyles = xmlDefinedStyles.map { x ->
            Style(
                x.attributes.getNamedItemNS("urn:oasis:names:tc:opendocument:xmlns:style:1.0", "name").textContent,
                true,
                null
            )
        }

        val xmlAtomaticStyles = mutableListOf<Node>()
        xmlStyles =
            document.descendants(nsOffice, "automatic-styles") //.descendants(nsOffice, "text").descendants(nsText, "p")
        for (xmlStyle in xmlStyles) {
            val styleNodes = XElement.toMutableList(
                xmlStyle.childNodes
            ).filter { x -> x.localName == "style" }
            xmlAtomaticStyles.addAll(styleNodes)

        }
        val automaticStyles = xmlAtomaticStyles.map { x ->
            Style(
                x.attributes.getNamedItemNS("urn:oasis:names:tc:opendocument:xmlns:style:1.0", "name").textContent,
                false,
                x.attributes.getNamedItem("style:parent-style-name")?.textContent
            )
        }

        val styles = automaticStyles.union(definedStyles)

        return styles.toList()
    }

    private fun assignAutomaticStyles(styles: List<Style>) {
        for (style in styles) {
            if (style.parentStyleName == null) {
                continue
            }

            val parentStyle = styles.firstOrNull { s ->
                s.name == style.parentStyleName && s.isBaseStyle
            }

            if (parentStyle != null) {
                style.parentStyle = parentStyle
            } else {
                logger.warn("ParentStyle '" + style.parentStyleName + "' used by '" + style.name + "' not found (or is no base style).")
            }
        }
    }

    private fun getRevisionStatus(style: Style): RevisionStatus {
        val styleName = style.name
        return when (styleName) {
            "ZZTitelGeprueft" -> RevisionStatus.Good
            "ZZTitelVerbesserungsbeduerftig" -> RevisionStatus.Improvable
            "ZZTitelUngeprueft" -> RevisionStatus.NotReviewed
            "ZZTitelMeilenstein" -> RevisionStatus.Milestone
            else -> RevisionStatus.Unknown
        }
    }

    private fun loadXML(file: File): XElement {
        logger.debug { "Loading XML file '$file'..." }

        //val xmlWriter = XmlWriter.Create(StringWriter(), XmlWriterSettings())
        val fileStream = Files.newInputStream(file.toPath())
        return XElement.load(fileStream)
    }

    private fun getParagraphs(doc: XElement): List<Paragraph> {
        logger.debug { "Getting paragraphs..." }

        val nsOffice = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"
        val nsText = "urn:oasis:names:tc:opendocument:xmlns:text:1.0"

        val xmlParagraphs = mutableListOf<Node>()

        val bodies = doc.descendants(nsOffice, "body") //.descendants(nsOffice, "text").descendants(nsText, "p")
        for (body in bodies) {
            val textNodes = XElement.toMutableList(body.childNodes)
                .filter { x -> x.localName == "text" }

            for (textNode in textNodes) {
                val pNodes = XElement.toMutableList(
                    textNode.childNodes
                ).filter { x -> x.localName == "p" }
                xmlParagraphs.addAll(pNodes)
            }
        }

        val paragraphs = xmlParagraphs.map { x ->
            Paragraph(
                x.textContent,
                x.attributes.getNamedItem("text:style-name").textContent
            )
        }

        return paragraphs.toList()
    }

    private fun checkChaptersErrors(chapters: List<Chapter>) {
        logger.debug { "Checking chapters for errors..." }

        for (chapter in chapters) {
            checkChapterErrors(chapters, chapter)
        }
    }

    private fun checkChapterErrors(chapters: List<Chapter>, chapter: Chapter) {
        checkReferences(chapters, chapter)
        checkTitle(chapter)
        checkSummary(chapter)
    }

    private fun checkSummary(chapter: Chapter): Boolean? {
        val success = chapter.summary.any { s -> !s.isBlank() }
        if (!success) {
            logger.info("Chapter '" + chapter.title + "' has no summary")
            chapter.debugInformation.add(Pair<DebugInformationType, Any?>(DebugInformationType.EmptySummary, null))
        }

        return (!success)
    }

    private fun checkTitle(chapter: Chapter): Boolean {
        if (chapter.title.isBlank()) {
            logger.info("Chapter between '" + chapter.precedingChapter!!.title + "' and '" + chapter.succeedingChapter!!.title + "' has no name.")
            chapter.debugInformation.add(Pair<DebugInformationType, Any?>(DebugInformationType.EmptyTitle, null))
            return false
        }

        return true
    }

    private fun checkReferences(chapters: List<Chapter>, chapter: Chapter): Boolean? {
        var failed = false

        for (sibling in chapter.precedingChapterReferences.union(chapter.succeedingChapterReferences)) {
            val exists = chapters.any { c -> c.title == sibling }
            if (!exists) {
                failed = true
                logger.info("Chapter '" + sibling + "' is referenced by '" + chapter.title + "' but does not exist.")
                chapter.debugInformation.add(
                    Pair<DebugInformationType, Any?>(
                        DebugInformationType.MissingReference,
                        sibling
                    )
                )
            }
        }

        return failed
    }
}


