//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.GUI;

import Book2Chart.GUI.GraphWindow;
import Book2Chart.Parser.Book;
import Book2Chart.Parser.FodtParser;


/**
* Interaktionslogik für MainWindow.xaml
*/
public class ExplorerWindow  extends Window 
{

    public ExplorerWindow() throws Exception {
        InitializeComponent();
    }

    private void load() throws Exception {
        // Configure open file dialog box
        Microsoft.Win32.OpenFileDialog dlg = new Microsoft.Win32.OpenFileDialog();
        dlg.FileName = "Book";
        // Default file name
        dlg.DefaultExt = ".fodt";
        // Default file extension
        dlg.Filter = "Flat ODT (.fodt)|*.fodt";
        // Filter files by extension
        // Show open file dialog box
        boolean? result = dlg.ShowDialog();
        // Process open file dialog box results
        if (result == true)
        {
            // Open document
            String filename = dlg.FileName;
            FodtParser parser = new Book2Chart.Parser.FodtParser();
            Book book = parser.parse(filename);
            this.DataContext = book;
        }
         
    }

    private void button_Click_1(Object sender, RoutedEventArgs e) throws Exception {
        new GraphWindow(DataContext instanceof Book ? (Book)DataContext : (Book)null).Show();
    }

    private void button_Click_Load(Object sender, RoutedEventArgs e) throws Exception {
        this.load();
    }

}


