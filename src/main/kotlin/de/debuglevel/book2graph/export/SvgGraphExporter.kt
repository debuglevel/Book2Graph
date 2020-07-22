package de.debuglevel.book2graph.export

import de.debuglevel.book2graph.book.Book
import de.debuglevel.book2graph.graph.GraphBuilder
import guru.nidi.graphviz.engine.Format
import mu.KotlinLogging
import java.io.File

object SvgGraphExporter {
    private val logger = KotlinLogging.logger {}

    fun export(book: Book, transitiveReduction: Boolean, svgFile: File) {
        logger.debug { "Exporting graph as SVG to '$svgFile'..." }

        val graph = GraphBuilder.build(book.chapters, transitiveReduction)
        val dot = DotExporter.export(graph)
        GraphvizExporter.export(dot, svgFile, Format.SVG)

        logger.debug { "Exported graph as SVG to '$svgFile'." }
    }
}