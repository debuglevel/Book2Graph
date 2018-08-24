//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.TUI;

import Book2Chart.TUI.Program;

public class Program   
{
    public static void main(String[] args) throws Exception {
        Program.Main(args);
    }

    static void Main(String[] args) throws Exception {
        FodtParser parser = new Book2Chart.Parser.FodtParser();
        parser.parse("Book.fodt");
        Console.ReadLine();
    }

}


