package de.debuglevel.book2graph.parser.graphvizCompatibility

class Graph<T : Any> {
    private val vertices = mutableListOf<Vertex<T>>()
    private val edges = mutableListOf<Edge<Vertex<T>>>()

    fun addVertex(obj: T, color: Color, shape: Shape): Vertex<T> {
        val vertex = Vertex(obj, color, shape)
        vertices.add(vertex)

        return vertex
    }

    fun addEdge(edge: Edge<Vertex<T>>) {
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
            s += "${vertex.hashCode()}[label=\"$vertex\",fillcolor=${vertex.color},style=filled,shape=${vertex.shape}];\n"
        }

        for (edge in edges) {
            s += "${edge.start.hashCode()} -> ${edge.end.hashCode()};\n"
        }

        s += "}\n"

        return s
    }

}
