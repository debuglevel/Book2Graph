package de.debuglevel.book2graph.cli

import de.debuglevel.book2graph.parser.GraphBuilder
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import java.io.File

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
        val dot = graph.generateDot()
        println(dot)
        println("===================================")

        println("Generating visualization of graph...")
        visualizeGraph(dot)
    }

    private fun visualizeGraph(dot: String) {
        val graph = guru.nidi.graphviz.parse.Parser.read(dot)
        Graphviz.fromGraph(graph)
                .render(Format.SVG)
                .toFile(File("Book.svg"))
    }
}


