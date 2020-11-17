package de.debuglevel.book2graph.graph

import mu.KotlinLogging

object GraphUtils {
    private val logger = KotlinLogging.logger {}

    /**
     * Whether another path between start and end (other than the given ignoredEdge) exists.
     */
    fun <T : Any> pathExists(start: Vertex<T>, end: Vertex<T>, ignoredEdge: Edge<T>) =
        findVertex(start, end, ignoredEdge)

    /**
     * Walk tree (starting from "start") to find vertex "breakCondition".
     */
    private fun <T : Any> findVertex(
        start: Vertex<T>,
        breakCondition: Vertex<T>,
        ignoredEdge: Edge<T>?
    ): Boolean {
        val descendants =
            when {
                // only filter if ignoredEdge is not null (i.e. we are on the first level of the recursive tree). Saves about 50% time.
                ignoredEdge != null -> start.outEdges
                    .filter { it !== ignoredEdge }
                    .map { it.end }
                else -> start.outEdges
                    .map { it.end }
            }

        return if (descendants.contains(breakCondition)) {
            true
        } else {
            descendants.any {
                // ignoredEdge is replaced by null here, as it is only relevant in the first call level of the recursion
                    descendant ->
                findVertex(descendant, breakCondition, null)
            }
        }
    }
}