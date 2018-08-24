//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.GUI;

import Book2Chart.Parser.Book;
import Book2Chart.Parser.Chapter;
import Book2Chart.Parser.GraphBuilder;


/**
* Interaktionslogik für GraphWindow.xaml
*/
public class GraphWindow  extends Window 
{

    private Graph<Chapter> __Graph = new Graph<Chapter>();
    public Graph<Chapter> getGraph() {
        return __Graph;
    }

    public void setGraph(Graph<Chapter> value) {
        __Graph = value;
    }

    public GraphWindow() throws Exception {
        InitializeComponent();
    }

    public GraphWindow(Book book) throws Exception {
        this();
        GraphBuilder graphBuilder = new GraphBuilder();
        this.setGraph(graphBuilder.CreateGraph(book.getChapters()));
        this.DataContext = this;
    }

    private void zoomcontrol_MouseWheel(Object sender, MouseWheelEventArgs e) throws Exception {
        if (e.Delta > 0)
        {
            if (this.zoomcontrol.Zoom >= this.zoomcontrol.MaxZoom)
            {
                return ;
            }
             
            this.zoomcontrol.Zoom += 0.1;
        }
        else if (e.Delta < 0)
        {
            if (this.zoomcontrol.Zoom <= this.zoomcontrol.MinZoom)
            {
                return ;
            }
             
            this.zoomcontrol.Zoom -= 0.1;
        }
          
    }

}


