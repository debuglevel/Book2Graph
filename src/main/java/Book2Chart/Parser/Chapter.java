//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:07
//

package Book2Chart.Parser;

import Book2Chart.Parser.Chapter;
import Book2Chart.Parser.DebugInformationType;
import Book2Chart.Parser.RevisionStatus;

public class Chapter   
{
    private String __Title = new String();
    public String getTitle() {
        return __Title;
    }

    public void setTitle(String value) {
        __Title = value;
    }

    private List<String> __Text = new List<String>();
    public List<String> getText() {
        return __Text;
    }

    public void setText(List<String> value) {
        __Text = value;
    }

    private List<String> __Comment = new List<String>();
    public List<String> getComment() {
        return __Comment;
    }

    public void setComment(List<String> value) {
        __Comment = value;
    }

    private List<String> __Summary = new List<String>();
    public List<String> getSummary() {
        return __Summary;
    }

    public void setSummary(List<String> value) {
        __Summary = value;
    }

    private List<String> __PrecedingChapterReferences = new List<String>();
    public List<String> getPrecedingChapterReferences() {
        return __PrecedingChapterReferences;
    }

    public void setPrecedingChapterReferences(List<String> value) {
        __PrecedingChapterReferences = value;
    }

    private List<String> __SucceedingChapterReferences = new List<String>();
    public List<String> getSucceedingChapterReferences() {
        return __SucceedingChapterReferences;
    }

    public void setSucceedingChapterReferences(List<String> value) {
        __SucceedingChapterReferences = value;
    }

    private Chapter __PrecedingChapter;
    public Chapter getPrecedingChapter() {
        return __PrecedingChapter;
    }

    public void setPrecedingChapter(Chapter value) {
        __PrecedingChapter = value;
    }

    private Chapter __SucceedingChapter;
    public Chapter getSucceedingChapter() {
        return __SucceedingChapter;
    }

    public void setSucceedingChapter(Chapter value) {
        __SucceedingChapter = value;
    }

    private List<KeyValuePair<DebugInformationType, Object>> __DebugInformation = new List<KeyValuePair<DebugInformationType, Object>>();
    public List<KeyValuePair<DebugInformationType, Object>> getDebugInformation() {
        return __DebugInformation;
    }

    public void setDebugInformation(List<KeyValuePair<DebugInformationType, Object>> value) {
        __DebugInformation = value;
    }

    private RevisionStatus __RevisionStatus = RevisionStatus.Unknown;
    public RevisionStatus getRevisionStatus() {
        return __RevisionStatus;
    }

    public void setRevisionStatus(RevisionStatus value) {
        __RevisionStatus = value;
    }

    public String getTextAsString() throws Exception {
        return String.Join(Environment.NewLine, this.getText());
    }

    public String getCommentAsString() throws Exception {
        return String.Join(Environment.NewLine, this.getComment());
    }

    public String getSummaryAsString() throws Exception {
        return String.Join(Environment.NewLine, this.getSummary());
    }

    public Chapter() throws Exception {
        this.setComment(new List<String>());
        this.setSummary(new List<String>());
        this.setText(new List<String>());
        this.setPrecedingChapterReferences(new List<String>());
        this.setSucceedingChapterReferences(new List<String>());
        this.setDebugInformation(new List<KeyValuePair<DebugInformationType, Object>>());
    }

}


