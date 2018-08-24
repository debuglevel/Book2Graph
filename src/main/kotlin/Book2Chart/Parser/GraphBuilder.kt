//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:08
//

package Book2Chart.Parser

import Book2Chart.Parser.Chapter
import Book2Chart.Parser.PrecedingArrow
import Book2Chart.Parser.SucceedingArrow
import CS2JNet.System.StringSupport

class GraphBuilder {
    @Throws(Exception::class)
    fun createGraph(chapters: IEnumerable<Chapter>): Graph<Chapter> {
        val graph = Graph<Chapter>()
        for (__dummyForeachVar0 in chapters) {
            val chapter = __dummyForeachVar0 as Chapter
            graph.AddVertex(chapter)
        }
        for (__dummyForeachVar3 in chapters) {
            val chapter = __dummyForeachVar3 as Chapter
            /* [UNSUPPORTED] 'var' as type is unsupported "var" */ precedingChapterString@ chapter.precedingChapterReferences
            while (true) {
                val preceedingChapter = this.findChapterByTitle(chapters, precedingChapterString)
                if (preceedingChapter != null) {
                    graph.AddEdge(Edge<Chapter>(preceedingChapter, chapter))
                }

            }
            /* [UNSUPPORTED] 'var' as type is unsupported "var" */ succeedingChapterString@ chapter.succeedingChapterReferences
            while (true) {
                val succeedingChapter = this.findChapterByTitle(chapters, succeedingChapterString)
                if (succeedingChapter != null) {
                    graph.AddEdge(Edge<Chapter>(chapter, succeedingChapter))
                }

            }
        }
        return graph
    }

    @Throws(Exception::class)
    private fun findChapterByTitle(chapters: IEnumerable<Chapter>, chapterTitle: String): Chapter? {
        return chapters.FirstOrDefault(/* [UNSUPPORTED] to translate lambda expressions we need an explicit delegate type, try adding a cast "(c) => {
            return StringSupport.equals(c.Title, chapterTitle);
        }" */)
    }

}


