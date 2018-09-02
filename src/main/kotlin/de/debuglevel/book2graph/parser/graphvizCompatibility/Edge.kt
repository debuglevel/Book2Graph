package de.debuglevel.book2graph.parser.graphvizCompatibility

data class Edge<T>(val start: T, val end: T) {
    override fun toString() = "$start -> $end"
}
