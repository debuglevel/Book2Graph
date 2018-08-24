//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.Parser

import Book2Chart.Parser.Style
import Book2Chart.Parser.StyleType

class Style {
    var name = String()

    var parentStyleName = String()

    var parentStyle: Style? = null

    var isBaseStyle: Boolean? = Boolean()

    var styleType = StyleType.Unkown

}


