//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.Parser

import Book2Chart.Parser.Book
import Book2Chart.Parser.Chapter
import Book2Chart.Parser.DebugInformationType
import Book2Chart.Parser.Paragraph
import Book2Chart.Parser.RevisionStatus
import Book2Chart.Parser.Style
import Book2Chart.Parser.StyleType
import CS2JNet.System.StringSupport

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
            val paragraph = __dummyForeachVar0 as Paragraph
            paragraph.style = this.getStyle(styles, paragraph)
            val styleType = paragraph.style!!.styleType
            if (styleType == StyleType.Title) {
                lastChapter = currentChapter
                currentChapter = Chapter()
                book.chapters.Add(currentChapter)
                currentChapter.precedingChapter = lastChapter
                lastChapter.succeedingChapter = currentChapter
                currentChapter.title = paragraph.content
                currentChapter.revisionStatus = this.getRevisionStatus(paragraph.style!!)
            } else if (styleType == StyleType.Successor) {
                currentChapter.succeedingChapterReferences.Add(paragraph.content)
            } else if (styleType == StyleType.Precessor) {
                currentChapter.precedingChapterReferences.Add(paragraph.content)
            } else if (styleType == StyleType.Summary) {
                currentChapter.summary.Add(paragraph.content)
            } else if (styleType == StyleType.Comment) {
                currentChapter.comment.Add(paragraph.content)
            } else if (styleType == StyleType.Content) {
                currentChapter.text.Add(paragraph.content)
            } else {
            }
        }
        this.checkChaptersErrors(book.chapters)
        return book
    }

    @Throws(Exception::class)
    private fun getStyle(styles: IEnumerable<Style>, paragraph: Paragraph): Style? {
        val styleName = paragraph.styleName
        val style = styles.FirstOrDefault(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(s) => {
            return StringSupport.equals(s.Name, styleName);
        }" */)
        return if (style.isBaseStyle!!) {
            style
        } else {
            style.parentStyle
        }
    }

    @Throws(Exception::class)
    private fun getStyleType(styleName: String): StyleType {
        return if (styleName.StartsWith("ZZTitel")) {
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
            StyleType.Unkown
        }
    }

    //Trace.TraceWarning("unknown style name used: " + paragraph.StyleName);
    //paragraph.DebugInformation.Add(new KeyValuePair<DebugInformationType, object>(DebugInformationType.UnknownStyle, paragraph.StyleName));
    @Throws(Exception::class)
    private fun getStyles(document: XElement): IEnumerable<Style> {
        val styles = this.getAllStyles(document)
        this.assignAutomaticStyles(styles)
        this.assignBaseStyleTypes(styles)
        return styles
    }

    @Throws(Exception::class)
    private fun assignBaseStyleTypes(styles: IEnumerable<Style>) {
        for (__dummyForeachVar1 in styles.Where(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(s) => {
            return s.IsBaseStyle;
        }" */)) {
            val style = __dummyForeachVar1 as Style
            style.styleType = this.getStyleType(style.name)
        }
    }

    @Throws(Exception::class)
    private fun getAllStyles(document: XElement): IEnumerable<Style> {
        val nsOffice = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"
        val nsStyle = "urn:oasis:names:tc:opendocument:xmlns:style:1.0"
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ xmlDefinedStyles = document.Descendants(nsOffice + "styles").Descendants(nsStyle + "style")
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ definedStyles
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ xmlAutomaticStyles = document.Descendants(nsOffice + "automatic-styles").Descendants(nsStyle + "style")
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ automaticStyles
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ styles = definedStyles.Concat(automaticStyles)
        return styles.ToList()
    }

    //var styles = (from item in xmlStyles
    //              select new Style
    //              {
    //                  Name = item.Attribute(nsStyle + "name").Value,
    //                  ParentStyleName = item.Attribute(nsStyle + "parent-style-name") != null ? item.Attribute(nsStyle + "parent-style-name").Value : null
    //              }).Where(s => s.ParentStyleName != null);
    @Throws(Exception::class)
    private fun assignAutomaticStyles(styles: IEnumerable<Style>) {
        for (__dummyForeachVar2 in styles) {
            val style = __dummyForeachVar2 as Style
            val parentStyle = styles.FirstOrDefault(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(s) => {
                return StringSupport.equals(s.Name, style.getParentStyleName()) && s.IsBaseStyle;
            }" */)
            if (parentStyle != null) {
                style.parentStyle = parentStyle
            } else {
                Trace.TraceWarning("ParentStyle '" + style.parentStyleName + "' used by '" + style.name + "' not found (or is no base style).")
            }
        }
    }

    @Throws(Exception::class)
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

    @Throws(Exception::class)
    private fun loadXML(filename: String): XElement {
        val xmlWriter = XmlWriter.Create(StringWriter(), XmlWriterSettings())
        val fileStream = FileStream(filename, FileMode.Open, FileAccess.Read, FileShare.ReadWrite)
        return XElement.Load(fileStream)
    }

    @Throws(Exception::class)
    private fun getParagraphs(doc: XElement): IEnumerable<Paragraph> {
        val nsOffice = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"
        val nsText = "urn:oasis:names:tc:opendocument:xmlns:text:1.0"
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ xmlParagraphs = doc.Descendants(nsOffice + "body").Descendants(nsOffice + "text").Descendants(nsText + "p")
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ paragraphs
        return paragraphs.ToList()
    }

    @Throws(Exception::class)
    private fun checkChaptersErrors(chapters: IEnumerable<Chapter>) {
        for (__dummyForeachVar3 in chapters) {
            val chapter = __dummyForeachVar3 as Chapter
            this.checkChapterErrors(chapters, chapter)
        }
    }

    @Throws(Exception::class)
    private fun checkChapterErrors(chapters: IEnumerable<Chapter>, chapter: Chapter) {
        this.checkReferences(chapters, chapter)
        this.checkTitle(chapter)
        this.checkSummary(chapter)
    }

    @Throws(Exception::class)
    private fun checkSummary(chapter: Chapter): Boolean? {
        val success = chapter.summary.Any(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(p) => {
            return String.IsNullOrWhiteSpace(p) == false;
        }" */)
        if (success == false) {
            Trace.TraceInformation("Chapter '" + chapter.title + "' has no summary")
            chapter.debugInformation.Add(KeyValuePair<DebugInformationType, Any>(DebugInformationType.EmptySummary, null))
        }

        return (!success)!!
    }

    @Throws(Exception::class)
    private fun checkTitle(chapter: Chapter): Boolean {
        if (String.IsNullOrWhiteSpace(chapter.title)) {
            Trace.TraceInformation("Chapter between '" + chapter.precedingChapter!!.title + "' and '" + chapter.succeedingChapter!!.title + "' has no name.")
            chapter.debugInformation.Add(KeyValuePair<DebugInformationType, Any>(DebugInformationType.EmptyTitle, null))
            return false
        }

        return true
    }

    @Throws(Exception::class)
    private fun checkReferences(chapters: IEnumerable<Chapter>, chapter: Chapter): Boolean? {
        var failed = false
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ sibling@ chapter.precedingChapterReferences.Concat(chapter.succeedingChapterReferences)
        while (true) {
            val exists = chapters.Any(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(x) => {
                return x.Title == sibling;
            }" */)
            if (exists == false) {
                failed = true
                Trace.TraceInformation("Chapter '" + sibling + "' by '" + chapter.title + "' referenced but does not exist.")
                chapter.debugInformation.Add(KeyValuePair<DebugInformationType, Any>(DebugInformationType.MissingReference, sibling))
            }

        }
        return failed
    }

}


