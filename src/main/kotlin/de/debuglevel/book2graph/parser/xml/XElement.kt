package de.debuglevel.book2graph.parser.xml

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class XElement {
    var document: Document? = null
    var element: Element? = null

    companion object {
        fun load(inputStream: InputStream): XElement {
            val dbFactory = DocumentBuilderFactory.newInstance()
            dbFactory.isNamespaceAware = true
            val dBuilder = dbFactory.newDocumentBuilder()
            val document = dBuilder.parse(inputStream)

            val xelement = XElement()
            xelement.document = document
            xelement.element = document.documentElement

            return xelement
        }
    }

    fun descendants(namespace: String, elementName: String): List<Node> {
        val nodeList = this.element?.getElementsByTagNameNS(namespace, elementName)!!
        return nodeList.toMutableList()
    }
}
