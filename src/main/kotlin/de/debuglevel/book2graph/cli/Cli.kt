package de.debuglevel.book2graph.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import de.debuglevel.book2graph.parser.GraphBuilder
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import mu.KotlinLogging
import java.io.File

class Cli : CliktCommand() {
    private val logger = KotlinLogging.logger {}

    private val fodtFile: File by option(help = "file name of FODT file").file(
        exists = true,
        folderOkay = false,
        readable = true
    ).default(File("Book.fodt"))

    private val transitiveReduction: Boolean by option(
        "--reduce",
        "-r",
        help = "perfom a transitive reduction on graph"
    )
        .flag(default = false)

    override fun run() {
        logger.info { "Starting Book2Graph..." }

        val parser = de.debuglevel.book2graph.parser.FodtParser()
        val book = parser.parse(fodtFile)

        val graph = GraphBuilder().createGraph(book.chapters, transitiveReduction)

        val dot = graph.generateDot()
        logger.debug {
            "Generated GraphViz dot source:" +
                    "===================================" +
                    dot +
                    "==================================="
        }

        visualizeGraph(dot)
    }

    private fun visualizeGraph(dot: String) {
        logger.debug { "Generating graph visualization..." }

        val graph = guru.nidi.graphviz.parse.Parser.read(dot)
        Graphviz.fromGraph(graph)
            .render(Format.SVG)
            .toFile(File("Book.svg"))

        logger.debug { "Generating graph visualization done." }
    }
}

fun main(args: Array<String>) = Cli().main(args)