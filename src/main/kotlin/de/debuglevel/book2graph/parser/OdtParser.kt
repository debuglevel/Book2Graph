package de.debuglevel.book2graph.parser

import de.debuglevel.book2graph.book.Book
import java.io.File

/**
 * Parses a ODT file with specified styles and creates a book object
 */
abstract class OdtParser {
    /**
     * Parses a given ODT file
     *
     * @param file the ODT file
     * @return a book object containing the ODT's information
     */
    abstract fun parse(file: File): Book
}