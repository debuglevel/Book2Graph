package de.debuglevel.book2graph.graph

data class Edge<T>(val start: T, val end: T) {
    override fun toString() = "$start -> $end"
}
