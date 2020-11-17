package de.debuglevel.book2graph.graph

data class Edge<T>(val start: Vertex<T>, val end: Vertex<T>) {
    override fun toString() = "$start -> $end"
}
