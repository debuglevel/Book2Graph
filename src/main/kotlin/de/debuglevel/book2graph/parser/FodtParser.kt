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
    @Throws(Exception::class)
    fun parse(filename: String): Book {
        val document = this.loadXML(filename)
        val styles = this.getStyles(document)
        val paragraphs = this.getParagraphs(document)
        val book = Book()
        var lastChapter = Chapter()
        var currentChapter = Chapter()
        for (__dummyForeachVar0 in paragraphs) {
            val paragraph = __dummyForeachVar0
            paragraph.style = this.getStyle(styles, paragraph)
            val styleType = paragraph.style!!.styleType
            if (styleType == StyleType.Title) {
                lastChapter = currentChapter
                currentChapter = Chapter()
                book.chapters.add(currentChapter)
                currentChapter.precedingChapter = lastChapter
                lastChapter.succeedingChapter = currentChapter
                currentChapter.title = paragraph.content
                currentChapter.revisionStatus = this.getRevisionStatus(paragraph.style!!)
            } else if (styleType == StyleType.Successor) {
                currentChapter.succeedingChapterReferences.add(paragraph.content)
            } else if (styleType == StyleType.Precessor) {
                currentChapter.precedingChapterReferences.add(paragraph.content)
            } else if (styleType == StyleType.Summary) {
                currentChapter.summary.add(paragraph.content)
            } else if (styleType == StyleType.Comment) {
                currentChapter.comment.add(paragraph.content)
            } else if (styleType == StyleType.Content) {
                currentChapter.text.add(paragraph.content)
            } else {
            }
        }
        this.checkChaptersErrors(book.chapters)
        return book
    }

    @Throws(Exception::class)
    private fun getStyle(styles: List<Style>, paragraph: Paragraph): Style? {
        //Trace.TraceInformation("getStyle: "+paragraph.styleName)
        val styleName = paragraph.styleName
        val style = styles.firstOrNull { s -> s.name == styleName }

        return if (style?.isBaseStyle!!) {
            style
        } else {
            style.parentStyle
        }
    }

    @Throws(Exception::class)
    private fun getStyleType(styleName: String): StyleType {
        return if (styleName.startsWith("ZZTitel")) {
            StyleType.Title
        } else if (StringSupport.equals(styleName, "ZZEinordnungDanach")) {
            StyleType.Successor
        } else if (StringSupport.equals(styleName, "ZZEinordnungVorher")) {
            StyleType.Precessor
        } else if (StringSupport.equals(styleName, "ZZZusammenfassung")) {
            StyleType.Summary
        } else if (StringSupport.equals(styleName, "ZZKommentar")) {
            StyleType.Comment
        } else if (StringSupport.equals(styleName, "ZZInhalt")) {
            StyleType.Content
        } else {
            //Trace.TraceWarning("unknown style name used: " + paragraph.StyleName);
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
        for (__dummyForeachVar1 in styles.filter { s -> s.isBaseStyle }) {
            val style = __dummyForeachVar1
            style.styleType = this.getStyleType(style.name)
        }
    }

    @Throws(Exception::class)
    private fun getAllStyles(document: XElement): List<Style> {
        val nsOffice = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"
        val nsStyle = "urn:oasis:names:tc:opendocument:xmlns:style:1.0"
        //var xmlDefinedStyles = document.Descendants(nsOffice + "styles").Descendants(nsStyle + "style")

        var xmlDefinedStyles = mutableListOf<Node>()

        var xmlStyles = document.Descendants(nsOffice, "styles") //.Descendants(nsOffice, "text").Descendants(nsText, "p")
        for (xmlStyle in xmlStyles)
        {
            var styleNodes = XElement.toMutableList(xmlStyle.childNodes).filter {
                x -> x.localName == "style"
            }
            xmlDefinedStyles.addAll(styleNodes)

        }



//        var definedStyles = listOf<Style>();
//        throw NotImplementedError()

        var definedStyles = xmlDefinedStyles.map {
            x-> Style(x.attributes.getNamedItemNS("urn:oasis:names:tc:opendocument:xmlns:style:1.0", "name").textContent, true, null)
        }

//        var definedStyles = from item in xmlDefinedStyles
//                select new Style
//        {
//            Name = item.Attribute(nsStyle + "name").Value,
//            IsBaseStyle = true
//        };


//        var xmlAutomaticStyles = document.Descendants(nsOffice + "automatic-styles").Descendants(nsStyle + "style")

//        var automaticStyles = listOf<Style>();
//        throw NotImplementedError()


        var xmlAtomaticStyles = mutableListOf<Node>()

        xmlStyles = document.Descendants(nsOffice, "automatic-styles") //.Descendants(nsOffice, "text").Descendants(nsText, "p")
        for (xmlStyle in xmlStyles)
        {
            var styleNodes = XElement.toMutableList(xmlStyle.childNodes).filter { x -> x.localName == "style" }
            xmlAtomaticStyles.addAll(styleNodes)

        }

        var automaticStyles = xmlAtomaticStyles.map {
            x-> Style(x.attributes.getNamedItemNS("urn:oasis:names:tc:opendocument:xmlns:style:1.0", "name").textContent,
                false,
                x.attributes.getNamedItem("style:parent-style-name")?.textContent
                )
        }

//        var automaticStyles = from item in xmlAutomaticStyles
//                select new Style
//        {
//            Name = item.Attribute(nsStyle + "name").Value,
//            ParentStyleName = item.Attribute(nsStyle + "parent-style-name") != null ? item.Attribute(nsStyle + "parent-style-name").Value : null,
//            IsBaseStyle = false
//        };

//        throw NotImplementedError()
        //var styles = definedStyles.Concat(automaticStyles)
        var styles = automaticStyles.union(definedStyles)

        //var styles = (from item in xmlStyles
        //              select new Style
        //              {
        //                  Name = item.Attribute(nsStyle + "name").Value,
        //                  ParentStyleName = item.Attribute(nsStyle + "parent-style-name") != null ? item.Attribute(nsStyle + "parent-style-name").Value : null
        //              }).Where(s => s.ParentStyleName != null);

        return styles.toList()
    }


    private fun assignAutomaticStyles(styles: List<Style>)
    {
        for (style in styles)
        {
            if (style.parentStyleName == null)
            {
                continue
            }

            val parentStyle = styles.firstOrNull { s ->
                StringSupport.equals(s.name, style.parentStyleName) && s.isBaseStyle
            }

            if (parentStyle != null) {
                style.parentStyle = parentStyle
            } else {
                Trace.TraceWarning("ParentStyle '" + style.parentStyleName + "' used by '" + style.name + "' not found (or is no base style).")
            }
        }
    }

    private fun getRevisionStatus(style: Style): RevisionStatus {
        val stylename = style.name
        if (StringSupport.equals(stylename, "ZZTitelGeprueft")) {
            return RevisionStatus.Good
        } else if (StringSupport.equals(stylename, "ZZTitelVerbesserungsbeduerftig")) {
            return RevisionStatus.Improvable
        } else if (StringSupport.equals(stylename, "ZZTitelUngeprueft")) {
            return RevisionStatus.Unreviewed
        } else if (StringSupport.equals(stylename, "ZZTitelMeilenstein")) {
            return RevisionStatus.Milestone
        }

        return RevisionStatus.Unknown
    }

    private fun loadXML(filename: String): XElement {
        //val xmlWriter = XmlWriter.Create(StringWriter(), XmlWriterSettings())
        val fileStream = Files.newInputStream(Paths.get(filename))
        return XElement.load(fileStream)
    }

    private fun getParagraphs(doc: XElement): List<Paragraph> {
        val nsOffice = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"
        val nsText = "urn:oasis:names:tc:opendocument:xmlns:text:1.0"

        //var xmlParagraphs = doc.Descendants(nsOffice, "body").Descendants(nsOffice, "text").Descendants(nsText, "p")

        val xmlParagraphs = mutableListOf<Node>()

        var bodies = doc.Descendants(nsOffice, "body") //.Descendants(nsOffice, "text").Descendants(nsText, "p")
        for (body in bodies)
        {
            var textNodes = XElement.toMutableList(body.childNodes).filter { x -> x.localName == "text" }

            for (textNode in textNodes)
            {
                var pNodes = XElement.toMutableList(textNode.childNodes).filter { x -> x.localName == "p" }
                xmlParagraphs.addAll(pNodes)
            }
        }

        //var paragraphs = mutableListOf<Paragraph>()

        var paragraphs = xmlParagraphs.map { x -> Paragraph(x.textContent, x.attributes.getNamedItem("text:style-name").textContent) }

        //throw NotImplementedError()
//        var paragraphs = from item in xmlParagraphs
//                select new Paragraph()
//        {
//            Content = item.value,
//            StyleName = item.attribute(nsText + "style-name").value
//        };

        return paragraphs.toList()
    }

    @Throws(Exception::class)
    private fun checkChaptersErrors(chapters: List<Chapter>) {
        for (__dummyForeachVar3 in chapters) {
            val chapter = __dummyForeachVar3
            this.checkChapterErrors(chapters, chapter)
        }
    }

    @Throws(Exception::class)
    private fun checkChapterErrors(chapters: List<Chapter>, chapter: Chapter) {
        this.checkReferences(chapters, chapter)
        this.checkTitle(chapter)
        this.checkSummary(chapter)
    }

    @Throws(Exception::class)
    private fun checkSummary(chapter: Chapter): Boolean? {
        val success = chapter.summary.any { s -> s.isBlank() == false }
        if (success == false) {
            Trace.TraceInformation("Chapter '" + chapter.title + "' has no summary")
            chapter.debugInformation.add(Pair<DebugInformationType, Any?>(DebugInformationType.EmptySummary, null))
        }

        return (!success)
    }

    @Throws(Exception::class)
    private fun checkTitle(chapter: Chapter): Boolean {
        if (chapter.title.isBlank()) {
            Trace.TraceInformation("Chapter between '" + chapter.precedingChapter!!.title + "' and '" + chapter.succeedingChapter!!.title + "' has no name.")
            chapter.debugInformation.add(Pair<DebugInformationType, Any?>(DebugInformationType.EmptyTitle, null))
            return false
        }

        return true
    }

    @Throws(Exception::class)
    private fun checkReferences(chapters: List<Chapter>, chapter: Chapter): Boolean? {
        var failed = false

        for (sibling in chapter.precedingChapterReferences.union(chapter.succeedingChapterReferences))
        {
            val exists = chapters.any { c -> c.title == sibling }
            if (exists == false) {
                failed = true
                Trace.TraceInformation("Chapter '" + sibling + "' is referenced by '" + chapter.title + "' but does not exist.")
                chapter.debugInformation.add(Pair<DebugInformationType, Any?>(DebugInformationType.MissingReference, sibling))
            }

        }
        return failed
    }

}


