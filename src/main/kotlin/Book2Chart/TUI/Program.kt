//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.TUI

import Book2Chart.TUI.Program

object Program {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        Program.Main(args)
    }

    @Throws(Exception::class)
    internal fun Main(args: Array<String>) {
        val parser = Book2Chart.Parser.FodtParser()
        parser.parse("Book.fodt")
        Console.ReadLine()
    }

}


