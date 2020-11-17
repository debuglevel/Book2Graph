package de.debuglevel.book2graph.graph

import mu.KotlinLogging

object TransitiveReduction {
    private val logger = KotlinLogging.logger {}

    /**
     * Perform an transitive reduction on the graph. All edges which provide shortcuts by bypassing a vertex will be removed. A graph with as few edges as possible is the result.
     * May stuck in a loop or throw an OverflowException if the graph is cyclic.
     */
    fun <T : Any> reduce(graph: Graph<T>) {
        logger.debug { "Performing transitive reduction on graph..." }

        for (edge in graph.getEdges()) {
            if (GraphUtils.pathExists(edge.start, edge.end, edge)) {
                graph.removeEdge(edge)
                logger.debug { "Removed superseded edge: $edge" }
            }
        }
    }
}