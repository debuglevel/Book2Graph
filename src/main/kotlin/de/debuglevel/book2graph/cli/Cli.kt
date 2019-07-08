package de.debuglevel.book2graph.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import de.debuglevel.book2graph.graph.GraphBuilder
import de.debuglevel.book2graph.graph.export.DotExporter
import de.debuglevel.book2graph.graph.export.GraphvizExporter
import de.debuglevel.book2graph.parser.FodtParser
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

        val book = FodtParser.parse(fodtFile)
        val graph = GraphBuilder.build(book.chapters, transitiveReduction)
        val dot = DotExporter.generate(graph)
        GraphvizExporter.render(dot, svgFile, Format.SVG)
    }
}

fun main(args: Array<String>) = Cli().main(args)