//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.Parser;

import Book2Chart.Parser.Style;
import Book2Chart.Parser.StyleType;

public class Style   
{
    private String __Name = new String();
    public String getName() {
        return __Name;
    }

    public void setName(String value) {
        __Name = value;
    }

    private String __ParentStyleName = new String();
    public String getParentStyleName() {
        return __ParentStyleName;
    }

    public void setParentStyleName(String value) {
        __ParentStyleName = value;
    }

    private Style __ParentStyle;
    public Style getParentStyle() {
        return __ParentStyle;
    }

    public void setParentStyle(Style value) {
        __ParentStyle = value;
    }

    private Boolean __IsBaseStyle = new Boolean();
    public Boolean getIsBaseStyle() {
        return __IsBaseStyle;
    }

    public void setIsBaseStyle(Boolean value) {
        __IsBaseStyle = value;
    }

    private StyleType __StyleType = StyleType.Unkown;
    public StyleType getStyleType() {
        return __StyleType;
    }

    public void setStyleType(StyleType value) {
        __StyleType = value;
    }

}


