package de.debuglevel.book2graph.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import de.debuglevel.book2graph.parser.GraphBuilder
import de.debuglevel.book2graph.visualizer.GraphVisualizer
import guru.nidi.graphviz.engine.Format
import mu.KotlinLogging
import java.io.File

class Cli : CliktCommand() {
    private val logger = KotlinLogging.logger {}

    private val fodtFile: File by option(
        "--inputFile",
        "-i",
        help = "file name of input FODT file"
    ).file(
        exists = true,
        folderOkay = false,
        readable = true
    ).default(File("Book.fodt"))

    private val svgFile: File by option(
        "--outputFile",
        "-o",
        help = "file name of output SVG file"
    ).file(
        folderOkay = false
    ).default(File("Book.svg"))

    private val transitiveReduction: Boolean by option(
        "--reduce",
        "-r",
        help = "perfom a transitive reduction on graph"
    ).flag(default = false)

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

        GraphVisualizer().render(dot, svgFile, Format.SVG)
    }
}

fun main(args: Array<String>) = Cli().main(args)