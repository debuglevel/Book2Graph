package Book2Chart.Parser

import Book2Chart.Parser.GraphvizCompatibility.Edge
import Book2Chart.Parser.GraphvizCompatibility.Graph

class GraphBuilder {
    fun createGraph(chapters: List<Chapter>): Graph<Chapter> {
        val graph = Graph<Chapter>()
        for (__dummyForeachVar0 in chapters) {
            val chapter = __dummyForeachVar0 as Chapter
            graph.AddVertex(chapter)
        }
        for (__dummyForeachVar3 in chapters) {
            val chapter = __dummyForeachVar3 as Chapter

            for (precedingChapterString in chapter.precedingChapterReferences) {
                val preceedingChapter = this.findChapterByTitle(chapters, precedingChapterString)
                if (preceedingChapter != null) {
                    graph.AddEdge(Edge<Chapter>(preceedingChapter, chapter))
                }

            }

            for (succeedingChapterString in chapter.succeedingChapterReferences) {
                val succeedingChapter = this.findChapterByTitle(chapters, succeedingChapterString)
                if (succeedingChapter != null) {
                    graph.AddEdge(Edge<Chapter>(chapter, succeedingChapter))
                }

            }
        }
        return graph
    }

    private fun findChapterByTitle(chapters: List<Chapter>, chapterTitle: String): Chapter? {
        return chapters.firstOrNull { c -> c.title == chapterTitle }
    }

}


