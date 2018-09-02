package de.debuglevel.book2graph.parser.graphvizCompatibility

class Graph<T : Any> {
    private val vertices = mutableListOf<T>()
    private val edges = mutableListOf<Edge<T>>()

    fun addVertex(chapter: T) {
        vertices.add(chapter)
    }

    fun addEdge(edge: Edge<T>) {
        edges.add(edge)
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
        var s = " digraph graphname {\n"

        for (vertex in vertices) {
            s += "${vertex.hashCode()}[label=\"$vertex\"];\n"
        }

        for (edge in edges) {
            s += "${edge.start.hashCode()} -> ${edge.end.hashCode()};\n"
        }

        s += "}\n"

        return s
    }

}
