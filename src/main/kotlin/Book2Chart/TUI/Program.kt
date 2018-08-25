package Book2Chart.TUI

object Program {
    @JvmStatic
    fun main(args: Array<String>) {
        Program.Main(args)
    }

    internal fun Main(args: Array<String>) {
        val parser = Book2Chart.Parser.FodtParser()
        var book = parser.parse("Book.fodt")
        readLine()
//        Console.ReadLine()
    }

}


