package de.debuglevel.book2graph.parser

class Style(var name: String, var isBaseStyle: Boolean = false, var parentStyleName: String?) {
    var parentStyle: Style? = null
    var styleType = StyleType.Unkown
}


