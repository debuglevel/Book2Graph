package de.debuglevel.book2graph.parser.xml

import de.debuglevel.book2graph.book.Style
import de.debuglevel.book2graph.parser.StyleParser
import mu.KotlinLogging
import org.w3c.dom.Node

class FodtStyleParser(private val document: XElement) : StyleParser() {
    private val logger = KotlinLogging.logger {}

    private val officeNamespace = "urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    private val textNamespace = "urn:oasis:names:tc:opendocument:xmlns:text:1.0"
    private val styleNamespace = "urn:oasis:names:tc:opendocument:xmlns:style:1.0"

    override fun getStyles(): List<Style> {
        logger.debug { "Getting styles..." }

        val userDefinedStyles = getUserDefinedStyles()
        val automaticGeneratedStyles = getAutomaticallyGeneratedStyles(userDefinedStyles)
        val styles = automaticGeneratedStyles.union(userDefinedStyles)

        logger.debug { "Got ${styles.size} styles." }
        return styles.toList()
    }

    /**
     * Finds and sets the styles types for user defined styles.
     */
    private fun assignUserDefinedStyleTypes(styles: List<Style>) {
        logger.trace { "Assigning style types to user defined styles..." }
        val userDefinedStyles = styles.filter { it.isUserDefinedStyle }
        for (userDefinedStyle in userDefinedStyles) {
            userDefinedStyle.styleType = getStyleType(userDefinedStyle.name)
        }
        logger.trace { "Assigned style types to user defined styles." }
    }

    /**
     * Finds and sets the styles types for automatically generated styles.
     */
    private fun assignAutomaticallyGeneratedStyles(styles: Set<Style>) {
        logger.trace { "Assigning style types to automatically generated styles..." }
        for (style in styles) {
            assignAutomaticallyGeneratedStyle(style, styles)
        }
        logger.trace { "Assigned style types to automatically generated styles." }
    }

    /**
     * Finds and sets the style types for a automatically generated style.
     */
    private fun assignAutomaticallyGeneratedStyle(style: Style, styles: Set<Style>) {
        // style has no parent style; nothing to do
        if (style.parentStyleName == null) {
            return
        }

        // find the user defined style with the according name
        val userDefinedStyle = styles.firstOrNull { s ->
            s.isUserDefinedStyle && s.name == style.parentStyleName
        }

        if (userDefinedStyle != null) {
            style.userDefinedStyle = userDefinedStyle
        } else {
            // warn if some inconsistency was found
            logger.warn("ParentStyle '${style.parentStyleName}' used by '${style.name}' not found (or is no base style).")
        }
    }

    /**
     * @param type "styles" or "automatic-styles"
     */
    private fun getXmlStyles(type: String): List<Node> {
        logger.trace { "Getting all styles of type '$type' defined in XML..." }

        val xmlStyles = document.descendants(officeNamespace, type)

        val xmlStyles2 = mutableListOf<Node>()
        for (xmlStyle in xmlStyles) {
            val styleNodes = xmlStyle.childNodes.asMutableList
                .filter { it.localName == "style" }
            xmlStyles2.addAll(styleNodes)
        }

        logger.trace { "Got ${xmlStyles2.size} styles of type '$type' defined in XML." }
        return xmlStyles2
    }

    private fun getUserDefinedStyles(): List<Style> {
        logger.trace { "Getting user defined styles..." }

        val xmlUserDefinedStyles = getXmlStyles("styles")
        val userDefinedStyles = xmlUserDefinedStyles.map {
            val styleName =
                it.attributes.getNamedItemNS(styleNamespace, "name").textContent
            Style(styleName, true, null)
        }

        assignUserDefinedStyleTypes(userDefinedStyles)

        logger.trace { "Got ${userDefinedStyles.size} user defined styles." }
        return userDefinedStyles
    }

    private fun getAutomaticallyGeneratedStyles(userDefinedStyles: List<Style>): List<Style> {
        logger.trace { "Getting automatic generated styles..." }

        val xmlAutomaticStyles = getXmlStyles("automatic-styles")
        val automaticGeneratedStyles = xmlAutomaticStyles.map {
            val styleName =
                it.attributes.getNamedItemNS(styleNamespace, "name").textContent
            val parentStyleName = it.attributes.getNamedItem("style:parent-style-name")?.textContent
            Style(styleName, false, parentStyleName)
        }

        val allStyles = automaticGeneratedStyles.union(userDefinedStyles)
        assignAutomaticallyGeneratedStyles(allStyles)

        logger.trace { "Got ${automaticGeneratedStyles.size} automatic generated styles." }
        return automaticGeneratedStyles
    }
}