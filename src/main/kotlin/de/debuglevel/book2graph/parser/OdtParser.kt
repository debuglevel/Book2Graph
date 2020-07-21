package de.debuglevel.book2graph.parser

import de.debuglevel.book2graph.book.Book
import java.io.File

abstract class OdtParser {
    /**
     * parses a given Flat ODT file
     *
     * @param filename the path to the FODT file
     * @return a book object containing the FODT's information
     */
    abstract fun parse(file: File): Book
}