package de.debuglevel.book2graph.parser.graphvizCompatibility

data class Vertex<T>(val vertex: T, val color: Color, val shape: Shape, val tooltip: String) {
    override fun toString() = vertex.toString()
}
