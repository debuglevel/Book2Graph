////
//// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
////
//
//package de.debuglevel.book2graph.gui
//
//import de.debuglevel.book2graph.gui.GraphWindow
//import de.debuglevel.book2graph.parser.Book
//import de.debuglevel.book2graph.parser.FodtParser
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
//            val parser = de.debuglevel.book2graph.parser.FodtParser()
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
