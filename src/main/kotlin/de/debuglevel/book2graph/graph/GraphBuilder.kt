package de.debuglevel.book2graph.graph

import mu.KotlinLogging
import kotlin.system.measureTimeMillis

class GraphBuilder<T : Any>(
    private val nodeInformationRetriever: NodeInformationRetriever<T>
) {
    private val logger = KotlinLogging.logger {}

    fun build(
        nodes: List<T>,
        transitiveReduction: Boolean
    ): Graph<T> {
        logger.debug { "Creating graph..." }

        val graph = Graph<T>()

        val vertices = nodes.map { node ->
            val color = nodeInformationRetriever.getColor(node)
            val shape = nodeInformationRetriever.getShape(node)
            val tooltip: String = nodeInformationRetriever.getTooltip(node)

            val vertex = Vertex(node, color, shape, tooltip)
            graph.addVertex(vertex)
            vertex
        }

        for (vertex in vertices) {
            nodeInformationRetriever.getPrecedingVertices(vertex, vertices)
                .forEach { precedingVertex -> graph.addEdge(Edge(precedingVertex, vertex)) }

            nodeInformationRetriever.getSucceedingVertices(vertex, vertices)
                .forEach { succeedingVertex -> graph.addEdge(Edge(vertex, succeedingVertex)) }
        }

        if (transitiveReduction) {
            val duration = measureTimeMillis { TransitiveReduction.reduce(graph) }
            logger.debug { "Removing superseded edges took ${duration}ms" }
        }

        logger.debug { "Created graph" }
        return graph
    }
}