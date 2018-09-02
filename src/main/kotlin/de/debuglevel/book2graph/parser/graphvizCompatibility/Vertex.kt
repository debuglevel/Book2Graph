package de.debuglevel.book2graph.parser.graphvizCompatibility

data class Vertex<T>(val vertex: T, val color: Color, val shape: Shape) {
    override fun toString() = vertex.toString()
}
