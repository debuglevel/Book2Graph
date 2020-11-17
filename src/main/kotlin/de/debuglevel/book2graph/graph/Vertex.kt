package de.debuglevel.book2graph.graph

data class Vertex<T>(val vertex: T, val color: Color, val shape: Shape, val tooltip: String) {
    val outEdges = hashSetOf<Edge<T>>()
    val inEdges = hashSetOf<Edge<T>>()
    override fun toString() = vertex.toString()
}
