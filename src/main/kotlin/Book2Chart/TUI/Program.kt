package Book2Chart.TUI

import Book2Chart.Parser.GraphBuilder

object Program {
    @JvmStatic
    fun main(args: Array<String>) {
        Program.Main(args)
    }

    internal fun Main(args: Array<String>) {
        val parser = Book2Chart.Parser.FodtParser()
        var book = parser.parse("Book.fodt")

        var graph = GraphBuilder().createGraph(book.chapters)
        println(graph.generateDot())
//        Console.ReadLine()
    }

}


