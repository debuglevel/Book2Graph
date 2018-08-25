package Book2Chart.Parser

class Paragraph(val content: String, val styleName: String)
{
    //var content = String()
    //var styleName = String()
    var style: Style? = null
    var debugInformation = mutableListOf<Pair<DebugInformationType, Any?>>()
}


