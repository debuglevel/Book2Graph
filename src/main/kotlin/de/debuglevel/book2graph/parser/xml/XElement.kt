package de.debuglevel.book2graph.parser.xml

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class XElement {
    var doc: Document? = null
    var element: Element? = null

    companion object {
        fun load(inputStream: InputStream): XElement {
            val dbFactory = DocumentBuilderFactory.newInstance()
            dbFactory.isNamespaceAware = true
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(inputStream)

            val xelement = XElement()
            xelement.doc = doc
            xelement.element = doc.documentElement

            return xelement
        }

        fun toMutableList(nodelist: NodeList): MutableList<Node> {
            val list = mutableListOf<Node>()
            for (idx in 0..(nodelist.length - 1)) {
                list.add(nodelist.item(idx))
            }
            return list
        }
    }

    fun descendants(namespace: String, elementName: String): List<Node> {
        val nodelist = this.element?.getElementsByTagNameNS(namespace, elementName)!!

        return toMutableList(nodelist)
    }
}
