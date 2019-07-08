package de.debuglevel.book2graph.graph.export

import de.debuglevel.book2graph.graph.Graph
import de.debuglevel.book2graph.parser.Chapter
import mu.KotlinLogging

object DotExporter {
    private val logger = KotlinLogging.logger {}

    fun generate(graph: Graph<Chapter>): String {
        logger.debug { "Generating GraphViz dot source..." }

        var s = " digraph graphname {\n"

        for (vertex in graph.getVertices()) {
            s += "${vertex.hashCode()}[label=\"$vertex\",fillcolor=${vertex.color.graphvizValue},style=filled,shape=${vertex.shape.graphvizValue},tooltip=\"${vertex.tooltip}\"];\n"
        }

        for (edge in graph.getEdges()) {
            s += "${edge.start.hashCode()} -> ${edge.end.hashCode()};\n"
        }

        s += "}\n"

        logger.debug { "Generating GraphViz dot source done." }
        return s
    }
}