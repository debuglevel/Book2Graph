////
//// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
////
//
//package Book2Chart.GUI
//
//import Book2Chart.GUI.GraphWindow
//import Book2Chart.Parser.Book
//import Book2Chart.Parser.FodtParser
//
//
///**
// * Interaktionslogik für MainWindow.xaml
// */
//class ExplorerWindow @Throws(Exception::class)
//constructor() : Window() {
//
//    init {
//        InitializeComponent()
//    }
//
//    @Throws(Exception::class)
//    private fun load() {
//        // Configure open file dialog box
//        val dlg = Microsoft.Win32.OpenFileDialog()
//        dlg.FileName = "Book"
//        // Default file name
//        dlg.DefaultExt = ".fodt"
//        // Default file extension
//        dlg.Filter = "Flat ODT (.fodt)|*.fodt"
//        // Filter files by extension
//        // Show open file dialog box
//
//        result = dlg.ShowDialog()
//        // Process open file dialog box results
//        if (result === true) {
//            // Open document
//            val filename = dlg.FileName
//            val parser = Book2Chart.Parser.FodtParser()
//            val book = parser.parse(filename)
//            this.DataContext = book
//        }
//
//    }
//
//    @Throws(Exception::class)
//    private fun button_Click_1(sender: Any, e: RoutedEventArgs) {
//        GraphWindow(if (DataContext is Book) DataContext as Book else null).Show()
//    }
//
//    @Throws(Exception::class)
//    private fun button_Click_Load(sender: Any, e: RoutedEventArgs) {
//        this.load()
//    }
//
//}
//
//
