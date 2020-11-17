package de.debuglevel.book2graph.graph

import de.debuglevel.book2graph.book.Chapter
import mu.KotlinLogging

object GraphUtils {
    private val logger = KotlinLogging.logger {}

    /**
     * Perform an transitive reduction on the graph. All edges which provide shortcuts by bypassing a vertex will be removed. A graph with as few edges as possible is the result.
     * May stuck in a loop or throw an OverflowException if the graph is cyclic.
     */
    fun transitiveReduction(graph: Graph<Chapter>) {
        logger.debug { "Performing transitive reduction on graph..." }

        for (edge in graph.getEdges()) {
            if (pathExists(edge.start, edge.end, edge)) {
                graph.removeEdge(edge)
                logger.debug { "Removed superseded edge: $edge" }
            }
        }
    }

    fun pathExists(start: Vertex<Chapter>, end: Vertex<Chapter>, ignoredEdge: Edge<Vertex<Chapter>>) =
        findVertex(start, end, ignoredEdge)

    /**
     * Walk tree (starting from "start") to find vertex "breakingCondition".
     */
    private fun findVertex(
        start: Vertex<Chapter>,
        breakingCondition: Vertex<Chapter>,
        ignoredEdge: Edge<Vertex<Chapter>>?
    ): Boolean {
        val descendants =
            when {
                // only filter if ignoredEdge is not null (i.e. we are on the first level of the recursive tree). Saved about 50% time.
                ignoredEdge != null -> start.outEdges
                    .filter { it !== ignoredEdge }
                    .map { it.end }
                else -> start.outEdges
                    .map { it.end }
            }

        if (descendants.contains(breakingCondition)) {
            return true
        }

        for (descendant in descendants) {
            // ignoredEdge is replaced by null here, as it is only relevant in the first call level of the recursion
            if (findVertex(descendant, breakingCondition, null)) {
                return true
            }
        }

        return false
    }
}