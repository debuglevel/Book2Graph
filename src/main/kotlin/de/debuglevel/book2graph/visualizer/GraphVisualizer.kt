package de.debuglevel.book2graph.visualizer

import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import mu.KotlinLogging
import java.io.File

class GraphVisualizer {
    private val logger = KotlinLogging.logger {}

    fun render(dot: String, outputFile: File, format: Format) {
        logger.debug { "Rendering graph visualization to file '$outputFile'..." }

        val graph = guru.nidi.graphviz.parse.Parser.read(dot)
        Graphviz.fromGraph(graph)
            .render(format)
            .toFile(outputFile)

        logger.debug { "Rendering graph visualization done." }
    }
}