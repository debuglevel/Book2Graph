package de.debuglevel.book2graph.parser

import de.debuglevel.book2graph.parser.graphvizCompatibility.Edge
import de.debuglevel.book2graph.parser.graphvizCompatibility.Graph

class GraphBuilder {
    fun createGraph(chapters: List<Chapter>): Graph<Chapter> {
        val graph = Graph<Chapter>()
        for (chapter in chapters) {
            graph.addVertex(chapter)
        }
        for (chapter in chapters) {
            for (precedingChapterString in chapter.precedingChapterReferences) {
                val precedingChapter = this.findChapterByTitle(chapters, precedingChapterString)
                if (precedingChapter != null) {
                    graph.addEdge(Edge(precedingChapter, chapter))
                }
            }

            for (succeedingChapterString in chapter.succeedingChapterReferences) {
                val succeedingChapter = this.findChapterByTitle(chapters, succeedingChapterString)
                if (succeedingChapter != null) {
                    graph.addEdge(Edge<Chapter>(chapter, succeedingChapter))
                }
            }
        }
        return graph
    }

    private fun findChapterByTitle(chapters: List<Chapter>, chapterTitle: String): Chapter? {
        return chapters.firstOrNull { c -> c.title == chapterTitle }
    }
}


