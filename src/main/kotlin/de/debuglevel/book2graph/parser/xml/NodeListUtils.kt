package de.debuglevel.book2graph.parser.xml

import org.w3c.dom.Node
import org.w3c.dom.NodeList

val NodeList.asList: List<Node>
    get() {
        val list = mutableListOf<Node>()
        for (index in 0 until this.length) {
            list.add(this.item(index))
        }

        return list.toList()
    }