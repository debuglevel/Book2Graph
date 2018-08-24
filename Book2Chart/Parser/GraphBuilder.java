//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.Parser;

import Book2Chart.Parser.Chapter;
import Book2Chart.Parser.PrecedingArrow;
import Book2Chart.Parser.SucceedingArrow;
import CS2JNet.System.StringSupport;

public class GraphBuilder   
{
    public Graph<Chapter> createGraph(IEnumerable<Chapter> chapters) throws Exception {
        Graph<Chapter> graph = new Graph<Chapter>();
        for (Object __dummyForeachVar0 : chapters)
        {
            Chapter chapter = (Chapter)__dummyForeachVar0;
            graph.AddVertex(chapter);
        }
        for (Object __dummyForeachVar3 : chapters)
        {
            Chapter chapter = (Chapter)__dummyForeachVar3;
            for (/* [UNSUPPORTED] 'var' as type is unsupported "var" */ precedingChapterString : chapter.getPrecedingChapterReferences())
            {
                Chapter preceedingChapter = this.findChapterByTitle(chapters, precedingChapterString);
                if (preceedingChapter != null)
                {
                    graph.AddEdge(new Edge<Chapter>(preceedingChapter, chapter));
                }
                 
            }
            for (/* [UNSUPPORTED] 'var' as type is unsupported "var" */ succeedingChapterString : chapter.getSucceedingChapterReferences())
            {
                Chapter succeedingChapter = this.findChapterByTitle(chapters, succeedingChapterString);
                if (succeedingChapter != null)
                {
                    graph.AddEdge(new Edge<Chapter>(chapter, succeedingChapter));
                }
                 
            }
        }
        return graph;
    }

    private Chapter findChapterByTitle(IEnumerable<Chapter> chapters, String chapterTitle) throws Exception {
        return chapters.FirstOrDefault(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(c) => {
            return StringSupport.equals(c.Title, chapterTitle);
        }" */);
    }

}


