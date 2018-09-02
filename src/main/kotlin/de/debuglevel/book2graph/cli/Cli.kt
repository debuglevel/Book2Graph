package de.debuglevel.book2graph.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import de.debuglevel.book2graph.parser.GraphBuilder
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import java.io.File

class Cli : CliktCommand() {
    private val fodtFile: File by option(help = "file name of FODT file").file(exists = true, folderOkay = false, readable = true).default(File("Book.fodt"))

    override fun run() {
        println("Starting Book2Graph...")
        println("Parsing file '$fodtFile'...")

        val parser = de.debuglevel.book2graph.parser.FodtParser()
        val book = parser.parse(fodtFile)

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

fun main(args: Array<String>) = Cli().main(args)