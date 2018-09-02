package de.debuglevel.book2graph.parser

object Trace {
    fun traceWarning(s: String) {
        println("Warning: $s")
    }

    fun traceInformation(s: String) {
        println("Info: " + s)
    }
}