package de.debuglevel.book2graph.cli

import de.debuglevel.book2graph.parser.GraphBuilder

object CLI {
    @JvmStatic
    fun main(args: Array<String>) {
        CLI.run()
    }

    private fun run() {
        val parser = de.debuglevel.book2graph.parser.FodtParser()
        val book = parser.parse("Book.fodt")

        val graph = GraphBuilder().createGraph(book.chapters)
        println()

        println("Generating graphviz.dot:")
        println("===================================")
        println(graph.generateDot())
        println("===================================")
    }
}


