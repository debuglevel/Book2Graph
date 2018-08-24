//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.Parser;

import Book2Chart.Parser.Book;
import Book2Chart.Parser.Chapter;
import Book2Chart.Parser.DebugInformationType;
import Book2Chart.Parser.Paragraph;
import Book2Chart.Parser.RevisionStatus;
import Book2Chart.Parser.Style;
import Book2Chart.Parser.StyleType;
import CS2JNet.System.StringSupport;

/**
* Parses a Flat ODT file with specified styles and creates a book object
*/
public class FodtParser   
{
    /**
    * parses a given Flat ODT file
    * 
    *  @param filename the path to the FODT file
    *  @return a book object containing the FODT's information
    */
    public Book parse(String filename) throws Exception {
        XElement document = this.loadXML(filename);
        IEnumerable<Style> styles = this.getStyles(document);
        IEnumerable<Paragraph> paragraphs = this.getParagraphs(document);
        Book book = new Book();
        Chapter lastChapter = new Chapter();
        Chapter currentChapter = new Chapter();
        for (Object __dummyForeachVar0 : paragraphs)
        {
            Paragraph paragraph = (Paragraph)__dummyForeachVar0;
            paragraph.setStyle(this.getStyle(styles, paragraph));
            StyleType styleType = paragraph.getStyle().getStyleType();
            if (styleType == StyleType.Title)
            {
                lastChapter = currentChapter;
                currentChapter = new Chapter();
                book.getChapters().Add(currentChapter);
                currentChapter.setPrecedingChapter(lastChapter);
                lastChapter.setSucceedingChapter(currentChapter);
                currentChapter.setTitle(paragraph.getContent());
                currentChapter.setRevisionStatus(this.getRevisionStatus(paragraph.getStyle()));
            }
            else if (styleType == StyleType.Successor)
            {
                currentChapter.getSucceedingChapterReferences().Add(paragraph.getContent());
            }
            else if (styleType == StyleType.Precessor)
            {
                currentChapter.getPrecedingChapterReferences().Add(paragraph.getContent());
            }
            else if (styleType == StyleType.Summary)
            {
                currentChapter.getSummary().Add(paragraph.getContent());
            }
            else if (styleType == StyleType.Comment)
            {
                currentChapter.getComment().Add(paragraph.getContent());
            }
            else if (styleType == StyleType.Content)
            {
                currentChapter.getText().Add(paragraph.getContent());
            }
            else
            {
            }      
        }
        this.checkChaptersErrors(book.getChapters());
        return book;
    }

    private Style getStyle(IEnumerable<Style> styles, Paragraph paragraph) throws Exception {
        String styleName = paragraph.getStyleName();
        Style style = styles.FirstOrDefault(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(s) => {
            return StringSupport.equals(s.Name, styleName);
        }" */);
        if (style.getIsBaseStyle())
        {
            return style;
        }
        else
        {
            return style.getParentStyle();
        } 
    }

    private StyleType getStyleType(String styleName) throws Exception {
        if (styleName.StartsWith("ZZTitel"))
        {
            return StyleType.Title;
        }
        else if (StringSupport.equals(styleName, "ZZEinordnungDanach"))
        {
            return StyleType.Successor;
        }
        else if (StringSupport.equals(styleName, "ZZEinordnungVorher"))
        {
            return StyleType.Precessor;
        }
        else if (StringSupport.equals(styleName, "ZZZusammenfassung"))
        {
            return StyleType.Summary;
        }
        else if (StringSupport.equals(styleName, "ZZKommentar"))
        {
            return StyleType.Comment;
        }
        else if (StringSupport.equals(styleName, "ZZInhalt"))
        {
            return StyleType.Content;
        }
        else
        {
            return StyleType.Unkown;
        }      
    }

    //Trace.TraceWarning("unknown style name used: " + paragraph.StyleName);
    //paragraph.DebugInformation.Add(new KeyValuePair<DebugInformationType, object>(DebugInformationType.UnknownStyle, paragraph.StyleName));
    private IEnumerable<Style> getStyles(XElement document) throws Exception {
        IEnumerable<Style> styles = this.getAllStyles(document);
        this.assignAutomaticStyles(styles);
        this.assignBaseStyleTypes(styles);
        return styles;
    }

    private void assignBaseStyleTypes(IEnumerable<Style> styles) throws Exception {
        for (Object __dummyForeachVar1 : styles.Where(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(s) => {
            return s.IsBaseStyle;
        }" */))
        {
            Style style = (Style)__dummyForeachVar1;
            style.setStyleType(this.getStyleType(style.getName()));
        }
    }

    private IEnumerable<Style> getAllStyles(XElement document) throws Exception {
        XNamespace nsOffice = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
        XNamespace nsStyle = "urn:oasis:names:tc:opendocument:xmlns:style:1.0";
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ xmlDefinedStyles = document.Descendants(nsOffice + "styles").Descendants(nsStyle + "style");
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ definedStyles;
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ xmlAutomaticStyles = document.Descendants(nsOffice + "automatic-styles").Descendants(nsStyle + "style");
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ automaticStyles;
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ styles = definedStyles.Concat(automaticStyles);
        return styles.ToList();
    }

    //var styles = (from item in xmlStyles
    //              select new Style
    //              {
    //                  Name = item.Attribute(nsStyle + "name").Value,
    //                  ParentStyleName = item.Attribute(nsStyle + "parent-style-name") != null ? item.Attribute(nsStyle + "parent-style-name").Value : null
    //              }).Where(s => s.ParentStyleName != null);
    private void assignAutomaticStyles(IEnumerable<Style> styles) throws Exception {
        for (Object __dummyForeachVar2 : styles)
        {
            Style style = (Style)__dummyForeachVar2;
            Style parentStyle = styles.FirstOrDefault(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(s) => {
                return StringSupport.equals(s.Name, style.getParentStyleName()) && s.IsBaseStyle;
            }" */);
            if (parentStyle != null)
            {
                style.setParentStyle(parentStyle);
            }
            else
            {
                Trace.TraceWarning("ParentStyle '" + style.getParentStyleName() + "' used by '" + style.getName() + "' not found (or is no base style).");
            } 
        }
    }

    private RevisionStatus getRevisionStatus(Style style) throws Exception {
        String stylename = style.getName();
        if (StringSupport.equals(stylename, "ZZTitelGeprueft"))
        {
            return RevisionStatus.Good;
        }
        else if (StringSupport.equals(stylename, "ZZTitelVerbesserungsbeduerftig"))
        {
            return RevisionStatus.Improvable;
        }
        else if (StringSupport.equals(stylename, "ZZTitelUngeprueft"))
        {
            return RevisionStatus.Unreviewed;
        }
        else if (StringSupport.equals(stylename, "ZZTitelMeilenstein"))
        {
            return RevisionStatus.Milestone;
        }
            
        return RevisionStatus.Unknown;
    }

    private XElement loadXML(String filename) throws Exception {
        XmlWriter xmlWriter = XmlWriter.Create(new StringWriter(), new XmlWriterSettings());
        FileStream fileStream = new FileStream(filename, FileMode.Open, FileAccess.Read, FileShare.ReadWrite);
        XElement document = XElement.Load(fileStream);
        return document;
    }

    private IEnumerable<Paragraph> getParagraphs(XElement doc) throws Exception {
        XNamespace nsOffice = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
        XNamespace nsText = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ xmlParagraphs = doc.Descendants(nsOffice + "body").Descendants(nsOffice + "text").Descendants(nsText + "p");
        /* [UNSUPPORTED] 'var' as type is unsupported "var" */ paragraphs;
        return paragraphs.ToList();
    }

    private void checkChaptersErrors(IEnumerable<Chapter> chapters) throws Exception {
        for (Object __dummyForeachVar3 : chapters)
        {
            Chapter chapter = (Chapter)__dummyForeachVar3;
            this.checkChapterErrors(chapters, chapter);
        }
    }

    private void checkChapterErrors(IEnumerable<Chapter> chapters, Chapter chapter) throws Exception {
        this.checkReferences(chapters, chapter);
        this.checkTitle(chapter);
        this.checkSummary(chapter);
    }

    private Boolean checkSummary(Chapter chapter) throws Exception {
        Boolean success = chapter.getSummary().Any(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(p) => {
            return String.IsNullOrWhiteSpace(p) == false;
        }" */);
        if (success == false)
        {
            Trace.TraceInformation("Chapter '" + chapter.getTitle() + "' has no summary");
            chapter.getDebugInformation().Add(new KeyValuePair<DebugInformationType, Object>(DebugInformationType.EmptySummary, null));
        }
         
        return !success;
    }

    private Boolean checkTitle(Chapter chapter) throws Exception {
        if (String.IsNullOrWhiteSpace(chapter.getTitle()))
        {
            Trace.TraceInformation("Chapter between '" + chapter.getPrecedingChapter().getTitle() + "' and '" + chapter.getSucceedingChapter().getTitle() + "' has no name.");
            chapter.getDebugInformation().Add(new KeyValuePair<DebugInformationType, Object>(DebugInformationType.EmptyTitle, null));
            return false;
        }
         
        return true;
    }

    private Boolean checkReferences(IEnumerable<Chapter> chapters, Chapter chapter) throws Exception {
        boolean failed = false;
        for (/* [UNSUPPORTED] 'var' as type is unsupported "var" */ sibling : chapter.getPrecedingChapterReferences().Concat(chapter.getSucceedingChapterReferences()))
        {
            boolean exists = chapters.Any(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(x) => {
                return x.Title == sibling;
            }" */);
            if (exists == false)
            {
                failed = true;
                Trace.TraceInformation("Chapter '" + sibling + "' by '" + chapter.getTitle() + "' referenced but does not exist.");
                chapter.getDebugInformation().Add(new KeyValuePair<DebugInformationType, Object>(DebugInformationType.MissingReference, sibling));
            }
             
        }
        return failed;
    }

}


