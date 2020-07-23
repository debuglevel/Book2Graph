package de.debuglevel.book2graph.book

class Style(
    var name: String,
    var isUserDefinedStyle: Boolean,
    var parentStyleName: String?
) {
    var userDefinedStyle: Style? = null
    var styleType = StyleType.Unknown
}


