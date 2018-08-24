//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:07
//

package Book2Chart.Parser;

import Book2Chart.Parser.Chapter;

public class Book   
{
    private List<Chapter> __Chapters = new List<Chapter>();
    public List<Chapter> getChapters() {
        return __Chapters;
    }

    public void setChapters(List<Chapter> value) {
        __Chapters = value;
    }

    public Book() throws Exception {
        this.setChapters(new List<Chapter>());
    }

}


