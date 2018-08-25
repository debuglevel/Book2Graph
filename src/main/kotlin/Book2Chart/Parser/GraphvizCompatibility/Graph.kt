package Book2Chart.Parser.GraphvizCompatibility

class Graph<T> {
    val vertices = mutableListOf<T>()
    val edges = mutableListOf<Edge<T>>()

    fun AddVertex(chapter: T) {
        vertices.add(chapter)
    }

    fun AddEdge(edge: Edge<T>) {
        edges.add(edge)
    }

    fun print() {
        for (vertex in vertices)
        {
            println(vertex.toString())
            edges
                    .filter { e -> e.start == vertex }
                    .forEach { e -> println("  ${e.end.toString()}") }
        }
    }

    fun generateDot(): String {
        var s = " digraph graphname {\n"

        for (vertex in vertices)
        {
            s += "${vertex?.hashCode()}[label=\"${vertex.toString()}\"];\n"

            edges
                    .filter { e -> e.start == vertex }
                    .forEach { e -> println("  ${e.end.toString()}") }
        }

        for(edge in edges)
        {
            s += "${edge.start.hashCode()} -> ${edge.end.hashCode()};\n"
        }

        s += "}\n"

        return s;
    }

}
