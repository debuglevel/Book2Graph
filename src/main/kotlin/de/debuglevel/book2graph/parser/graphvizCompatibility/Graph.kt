package de.debuglevel.book2graph.parser.graphvizCompatibility

import mu.KotlinLogging

class Graph<T : Any> {
    private val logger = KotlinLogging.logger {}

    private val vertices = mutableSetOf<Vertex<T>>()
    private val edges = mutableSetOf<Edge<Vertex<T>>>()

    fun addVertex(obj: T, color: Color, shape: Shape, tooltip: String): Vertex<T> {
        val vertex = Vertex(obj, color, shape, tooltip)
        vertices.add(vertex)

        return vertex
    }

    fun addEdge(edge: Edge<Vertex<T>>) {
        edges.add(edge)
        edge.start.outEdges.add(edge)
        edge.end.inEdges.add(edge)
    }

    fun removeEdge(edge: Edge<Vertex<T>>) {
        edges.remove(edge)
        edge.start.outEdges.remove(edge)
        edge.end.inEdges.remove(edge)
    }

    fun getEdges(): Set<Edge<Vertex<T>>> {
        return edges.toSet()
    }

    fun print() {
        for (vertex in vertices) {
            println(vertex.toString())
            edges
                .filter { e -> e.start == vertex }
                .forEach { e -> println("  ${e.end}") }
        }
    }

    fun generateDot(): String {
        logger.debug { "Generating GraphViz dot source..." }

        var s = " digraph graphname {\n"

        for (vertex in vertices) {
            s += "${vertex.hashCode()}[label=\"$vertex\",fillcolor=${vertex.color},style=filled,shape=${vertex.shape},tooltip=\"${vertex.tooltip}\"];\n"
        }

        for (edge in edges) {
            s += "${edge.start.hashCode()} -> ${edge.end.hashCode()};\n"
        }

        s += "}\n"

        logger.debug { "Generating GraphViz dot source done." }
        return s
    }

}
