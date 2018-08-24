//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.Parser;

import Book2Chart.Parser.DebugInformationType;
import Book2Chart.Parser.Style;

public class Paragraph   
{
    private String __Content = new String();
    public String getContent() {
        return __Content;
    }

    public void setContent(String value) {
        __Content = value;
    }

    private String __StyleName = new String();
    public String getStyleName() {
        return __StyleName;
    }

    public void setStyleName(String value) {
        __StyleName = value;
    }

    private Style __Style;
    public Style getStyle() {
        return __Style;
    }

    public void setStyle(Style value) {
        __Style = value;
    }

    private List<KeyValuePair<DebugInformationType, Object>> __DebugInformation = new List<KeyValuePair<DebugInformationType, Object>>();
    public List<KeyValuePair<DebugInformationType, Object>> getDebugInformation() {
        return __DebugInformation;
    }

    public void setDebugInformation(List<KeyValuePair<DebugInformationType, Object>> value) {
        __DebugInformation = value;
    }

    public Paragraph() throws Exception {
        this.setDebugInformation(new List<KeyValuePair<DebugInformationType, Object>>());
    }

}


