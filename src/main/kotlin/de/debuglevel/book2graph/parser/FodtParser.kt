package de.debuglevel.book2graph.parser

import org.w3c.dom.Node
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Parses a Flat ODT file with specified styles and creates a book object
 */
class FodtParser {
    /**
     * parses a given Flat ODT file
     *
     * @param filename the path to the FODT file
     * @return a book object containing the FODT's information
     */
    fun parse(filename: String): Book {
        val document = this.loadXML(filename)
        val styles = this.getStyles(document)
        val paragraphs = this.getParagraphs(document)

        val book = Book()
        var lastChapter = Chapter()
        var currentChapter = Chapter()

        for (paragraph in paragraphs) {
            paragraph.style = this.getStyle(styles, paragraph)
            val styleType = paragraph.style!!.styleType
            when (styleType) {
                StyleType.Title -> {
                    lastChapter = currentChapter
                    currentChapter = Chapter()
                    book.chapters.add(currentChapter)
                    currentChapter.precedingChapter = lastChapter
                    lastChapter.succeedingChapter = currentChapter
                    currentChapter.title = paragraph.content
                    currentChapter.revisionStatus = this.getRevisionStatus(paragraph.style!!)
                }
                StyleType.Successor -> currentChapter.succeedingChapterReferences.add(paragraph.content)
                StyleType.Precessor -> currentChapter.precedingChapterReferences.add(paragraph.content)
                StyleType.Summary -> currentChapter.summary.add(paragraph.content)
                StyleType.Comment -> currentChapter.comment.add(paragraph.content)
                StyleType.Content -> currentChapter.text.add(paragraph.content)
                else -> {
                }
            }
        }

        this.checkChaptersErrors(book.chapters)

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
            StringSupport.equals(styleName, "ZZEinordnungDanach") -> StyleType.Successor
            StringSupport.equals(styleName, "ZZEinordnungVorher") -> StyleType.Precessor
            StringSupport.equals(styleName, "ZZZusammenfassung") -> StyleType.Summary
            StringSupport.equals(styleName, "ZZKommentar") -> StyleType.Comment
            StringSupport.equals(styleName, "ZZInhalt") -> StyleType.Content
            else -> //Trace.traceWarning("unknown style name used: " + paragraph.StyleName);
                //paragraph.DebugInformation.Add(new KeyValuePair<DebugInformationType, object>(DebugInformationType.UnknownStyle, paragraph.StyleName));
                StyleType.Unkown
        }
    }

    private fun getStyles(document: XElement): List<Style> {
        val styles = this.getAllStyles(document)
        this.assignAutomaticStyles(styles)
        this.assignBaseStyleTypes(styles)
        return styles
    }

    private fun assignBaseStyleTypes(styles: List<Style>) {
        for (style in styles.filter { s -> s.isBaseStyle }) {
            style.styleType = this.getStyleType(style.name)
        }
    }

    private fun getAllStyles(document: XElement): List<Style> {
        val nsOffice = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"
        val nsStyle = "urn:oasis:names:tc:opendocument:xmlns:style:1.0"

        val xmlDefinedStyles = mutableListOf<Node>()
        var xmlStyles = document.descendants(nsOffice, "styles") //.descendants(nsOffice, "text").descendants(nsText, "p")
        for (xmlStyle in xmlStyles) {
            val styleNodes = XElement.toMutableList(xmlStyle.childNodes).filter { x ->
                x.localName == "style"
            }
            xmlDefinedStyles.addAll(styleNodes)

        }
        val definedStyles = xmlDefinedStyles.map { x ->
            Style(x.attributes.getNamedItemNS("urn:oasis:names:tc:opendocument:xmlns:style:1.0", "name").textContent, true, null)
        }

        val xmlAtomaticStyles = mutableListOf<Node>()
        xmlStyles = document.descendants(nsOffice, "automatic-styles") //.descendants(nsOffice, "text").descendants(nsText, "p")
        for (xmlStyle in xmlStyles) {
            val styleNodes = XElement.toMutableList(xmlStyle.childNodes).filter { x -> x.localName == "style" }
            xmlAtomaticStyles.addAll(styleNodes)

        }
        val automaticStyles = xmlAtomaticStyles.map { x ->
            Style(x.attributes.getNamedItemNS("urn:oasis:names:tc:opendocument:xmlns:style:1.0", "name").textContent,
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
                StringSupport.equals(s.name, style.parentStyleName) && s.isBaseStyle
            }

            if (parentStyle != null) {
                style.parentStyle = parentStyle
            } else {
                Trace.traceWarning("ParentStyle '" + style.parentStyleName + "' used by '" + style.name + "' not found (or is no base style).")
            }
        }
    }

    private fun getRevisionStatus(style: Style): RevisionStatus {
        val styleName = style.name
        return when {
            StringSupport.equals(styleName, "ZZTitelGeprueft") -> RevisionStatus.Good
            StringSupport.equals(styleName, "ZZTitelVerbesserungsbeduerftig") -> RevisionStatus.Improvable
            StringSupport.equals(styleName, "ZZTitelUngeprueft") -> RevisionStatus.Unreviewed
            StringSupport.equals(styleName, "ZZTitelMeilenstein") -> RevisionStatus.Milestone
            else -> RevisionStatus.Unknown
        }

    }

    private fun loadXML(filename: String): XElement {
        //val xmlWriter = XmlWriter.Create(StringWriter(), XmlWriterSettings())
        val fileStream = Files.newInputStream(Paths.get(filename))
        return XElement.load(fileStream)
    }

    private fun getParagraphs(doc: XElement): List<Paragraph> {
        val nsOffice = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"
        val nsText = "urn:oasis:names:tc:opendocument:xmlns:text:1.0"

        val xmlParagraphs = mutableListOf<Node>()

        val bodies = doc.descendants(nsOffice, "body") //.descendants(nsOffice, "text").descendants(nsText, "p")
        for (body in bodies) {
            val textNodes = XElement.toMutableList(body.childNodes).filter { x -> x.localName == "text" }

            for (textNode in textNodes) {
                val pNodes = XElement.toMutableList(textNode.childNodes).filter { x -> x.localName == "p" }
                xmlParagraphs.addAll(pNodes)
            }
        }

        val paragraphs = xmlParagraphs.map { x -> Paragraph(x.textContent, x.attributes.getNamedItem("text:style-name").textContent) }

        return paragraphs.toList()
    }

    private fun checkChaptersErrors(chapters: List<Chapter>) {
        for (chapter in chapters) {
            this.checkChapterErrors(chapters, chapter)
        }
    }

    private fun checkChapterErrors(chapters: List<Chapter>, chapter: Chapter) {
        this.checkReferences(chapters, chapter)
        this.checkTitle(chapter)
        this.checkSummary(chapter)
    }

    private fun checkSummary(chapter: Chapter): Boolean? {
        val success = chapter.summary.any { s -> !s.isBlank() }
        if (!success) {
            Trace.traceInformation("Chapter '" + chapter.title + "' has no summary")
            chapter.debugInformation.add(Pair<DebugInformationType, Any?>(DebugInformationType.EmptySummary, null))
        }

        return (!success)
    }

    private fun checkTitle(chapter: Chapter): Boolean {
        if (chapter.title.isBlank()) {
            Trace.traceInformation("Chapter between '" + chapter.precedingChapter!!.title + "' and '" + chapter.succeedingChapter!!.title + "' has no name.")
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
                Trace.traceInformation("Chapter '" + sibling + "' is referenced by '" + chapter.title + "' but does not exist.")
                chapter.debugInformation.add(Pair<DebugInformationType, Any?>(DebugInformationType.MissingReference, sibling))
            }
        }

        return failed
    }
}


