package de.debuglevel.book2graph.graph

data class Vertex<T>(val vertex: T, val color: Color, val shape: Shape, val tooltip: String) {
    val outEdges = hashSetOf<Edge<Vertex<T>>>()
    val inEdges = hashSetOf<Edge<Vertex<T>>>()
    override fun toString() = vertex.toString()
}
