package de.debuglevel.book2graph.parser

import de.debuglevel.book2graph.book.Book
import de.debuglevel.book2graph.book.Paragraph
import de.debuglevel.book2graph.book.RevisionStatus
import de.debuglevel.book2graph.book.Style
import mu.KotlinLogging
import java.io.File

/**
 * Parses a ODT file with specified styles and creates a book object
 */
abstract class OdtParser {
    private val logger = KotlinLogging.logger {}

    /**
     * Parses a given ODT file
     *
     * @param file the ODT file
     * @return a book object containing the ODT's information
     */
    abstract fun parse(file: File): Book

    /**
     * Gets the revision status based on the style name (by convention).
     */
    protected fun getRevisionStatus(style: Style): RevisionStatus {
        return when (style.name) {
            "ZZTitelGeprueft" -> RevisionStatus.Good
            "ZZTitelVerbesserungsbeduerftig" -> RevisionStatus.Improvable
            "ZZTitelUngeprueft" -> RevisionStatus.NotReviewed
            "ZZTitelMeilenstein" -> RevisionStatus.Milestone
            else -> RevisionStatus.Unknown
        }
    }

    /**
     * Gets the styles in this document (user defined and automatically generated).
     */
    protected abstract fun getStyles(): List<Style>

    /**
     * Loads the ODT document to initialize the parser.
     */
    protected abstract fun loadDocument(file: File)

    /**
     * Gets the paragraphs in a document.
     */
    protected abstract fun getParagraphs(): List<Paragraph>
}