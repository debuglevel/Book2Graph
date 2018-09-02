package de.debuglevel.book2graph.tui

import de.debuglevel.book2graph.parser.GraphBuilder

object Program {
    @JvmStatic
    fun main(args: Array<String>) {
        Program.Main(args)
    }

    internal fun Main(args: Array<String>) {
        val parser = de.debuglevel.book2graph.parser.FodtParser()
        var book = parser.parse("Book.fodt")


        var graph = GraphBuilder().createGraph(book.chapters)
        println()

        println("Generating graphviz.dot:")
        println("===================================")
        println(graph.generateDot())
        println("===================================")
//        Console.ReadLine()
    }

}


