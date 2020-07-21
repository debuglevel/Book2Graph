package de.debuglevel.book2graph.book

import de.debuglevel.book2graph.parser.Style

class Paragraph(val content: String, val styleName: String) {
    var style: Style? = null
    var debugInformation = mutableListOf<Pair<DebugInformationType, Any?>>()
}


