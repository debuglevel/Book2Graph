package Book2Chart.Parser

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import java.io.StringReader
import javax.xml.parsers.DocumentBuilder




class XElement {

    var doc: Document? = null
    var element: Element? = null

    companion object {
        fun load(inputStream: InputStream): XElement
        {

//            val xml = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//                    + "<ns1:schema xmlns:ns1='http://example.com'>"
//                    + "<ns1:tag1>"
//                    + "<ns1:tag2>value</ns1:tag2>"
//                    + "</ns1:tag1>"
//                    + "</ns1:schema>")
//
//            val docBuilderFactory = DocumentBuilderFactory.newInstance()
//            docBuilderFactory.isNamespaceAware = true
//            val docBuilder = docBuilderFactory.newDocumentBuilder()
//            val doc2 = docBuilder.parse(InputSource(StringReader(xml)))
//
//            val nl = doc2.getElementsByTagNameNS("http://example.com", "tag2")
//            nl.length




            val dbFactory = DocumentBuilderFactory.newInstance()
            dbFactory.setNamespaceAware(true);
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(inputStream)

            var xelement = XElement()
            xelement.doc = doc
            xelement.element = doc.documentElement



            return xelement
        }

        fun toMutableList(nodeliste: NodeList): MutableList<Node> {
            var list = mutableListOf<Node>()
            for (idx in 0..(nodeliste.length - 1)) {
                list.add(nodeliste.item(idx))
            }
            return list
        }
    }

    fun Descendants(namespace: String, elementName: String): List<Node> {

        var nodeliste = this.element?.getElementsByTagNameNS(namespace, elementName)!!

        var list = toMutableList(nodeliste)

        return list
    }



    fun value(): String
    {
        throw  NotImplementedError()
    }

    fun attribute(name: String): XAttribute
    {
        throw  NotImplementedError()
    }
}
