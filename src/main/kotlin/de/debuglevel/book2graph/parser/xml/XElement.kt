package de.debuglevel.book2graph.parser.xml

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class XElement(
    private val document: Document,
    private val element: Element
) {
    companion object {
        fun load(inputStream: InputStream): XElement {
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            documentBuilderFactory.isNamespaceAware = true
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val document = documentBuilder.parse(inputStream)

            val xelement = XElement(document, document.documentElement)

            return xelement
        }
    }

    fun descendants(namespace: String, elementName: String): List<Node> {
        val nodeList = this.element.getElementsByTagNameNS(namespace, elementName)!!
        return nodeList.asList
    }
}
