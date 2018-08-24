//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.Parser

import Book2Chart.Parser.DebugInformationType
import Book2Chart.Parser.Style

class Paragraph @Throws(Exception::class)
constructor() {
    var content = String()

    var styleName = String()

    var style: Style? = null

    var debugInformation = List<KeyValuePair<DebugInformationType, Any>>()

    init {
        this.debugInformation = List<KeyValuePair<DebugInformationType, Any>>()
    }

}


