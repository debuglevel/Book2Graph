package de.debuglevel.book2graph.parser

import de.debuglevel.book2graph.book.Style
import de.debuglevel.book2graph.book.StyleType
import mu.KotlinLogging

abstract class StyleParser {
    private val logger = KotlinLogging.logger {}

    /**
     * Gets the Style defined by a style name.
     */
    fun getStyleType(styleName: String): StyleType {
        logger.trace { "Getting style enum for style with name '$styleName'..." }

        return when {
            styleName.startsWith("ZZTitel") -> StyleType.Title
            styleName == "ZZEinordnungDanach" -> StyleType.Successor
            styleName == "ZZEinordnungVorher" -> StyleType.Predecessor
            styleName == "ZZZusammenfassung" -> StyleType.Summary
            styleName == "ZZKommentar" -> StyleType.Comment
            styleName == "ZZInhalt" -> StyleType.Content
            else -> {
                logger.trace { "unknown style name used: $styleName" }
                //paragraph.DebugInformation.Add(new KeyValuePair<DebugInformationType, object>(DebugInformationType.UnknownStyle, paragraph.StyleName));
                StyleType.Unknown
            }
        }
    }

    /**
     * Gets user defined styles and automatically generated styles.
     */
    abstract fun getStyles(): List<Style>
}