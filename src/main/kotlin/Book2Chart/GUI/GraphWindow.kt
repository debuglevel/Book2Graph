////
//// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
////
//
//package Book2Chart.GUI
//
//import Book2Chart.Parser.Book
//import Book2Chart.Parser.Chapter
//import Book2Chart.Parser.GraphBuilder
//
//
///**
// * Interaktionslogik für GraphWindow.xaml
// */
//class GraphWindow @Throws(Exception::class)
//constructor() : Window() {
//
//    var graph = Graph<Chapter>()
//
//    init {
//        InitializeComponent()
//    }
//
//    @Throws(Exception::class)
//    constructor(book: Book) : this() {
//        val graphBuilder = GraphBuilder()
//        this.graph = graphBuilder.CreateGraph(book.chapters)
//        this.DataContext = this
//    }
//
//    @Throws(Exception::class)
//    private fun zoomcontrol_MouseWheel(sender: Any, e: MouseWheelEventArgs) {
//        if (e.Delta > 0) {
//            if (this.zoomcontrol.Zoom >= this.zoomcontrol.MaxZoom) {
//                return
//            }
//
//            this.zoomcontrol.Zoom += 0.1
//        } else if (e.Delta < 0) {
//            if (this.zoomcontrol.Zoom <= this.zoomcontrol.MinZoom) {
//                return
//            }
//
//            this.zoomcontrol.Zoom -= 0.1
//        }
//
//    }
//
//}
//
//
