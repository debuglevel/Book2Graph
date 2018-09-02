package de.debuglevel.book2graph.parser

import de.debuglevel.book2graph.parser.graphvizCompatibility.*

class GraphBuilder {
    fun createGraph(chapters: List<Chapter>): Graph<Chapter> {
        val graph = Graph<Chapter>()

        val chapterVertices = mutableListOf<Vertex<Chapter>>()
        for (chapter in chapters) {
            val color = when {
                chapter.revisionStatus == RevisionStatus.Good -> Color.palegreen1
                chapter.revisionStatus == RevisionStatus.Improvable -> Color.palevioletred1
                chapter.revisionStatus == RevisionStatus.Unreviewed -> Color.palegoldenrod
                chapter.revisionStatus == RevisionStatus.Milestone -> Color.skyblue1
                chapter.revisionStatus == RevisionStatus.Unknown -> Color.gray92
                else -> Color.gray92
            }

            val shape = when {
                chapter.revisionStatus == RevisionStatus.Milestone -> Shape.rectangle
                else -> Shape.ellipse
            }

            val tooltip = chapter.summaryAsString.replace("\n", "&#10;")

            chapterVertices.add(graph.addVertex(chapter, color, shape, tooltip))
        }

        for (chapterVertex in chapterVertices) {
            for (precedingChapterString in chapterVertex.vertex.precedingChapterReferences) {
                val precedingChapter = this.findChapterByTitle(chapters, precedingChapterString)
                val precedingChapterVertex = chapterVertices.firstOrNull { it.vertex == precedingChapter }
                if (precedingChapterVertex != null) {
                    graph.addEdge(Edge(precedingChapterVertex, chapterVertex))
                }
            }

            for (succeedingChapterString in chapterVertex.vertex.succeedingChapterReferences) {
                val succeedingChapter = this.findChapterByTitle(chapters, succeedingChapterString)
                val succeedingChapterVertex = chapterVertices.firstOrNull { it.vertex == succeedingChapter }
                if (succeedingChapterVertex != null) {
                    graph.addEdge(Edge(chapterVertex, succeedingChapterVertex))
                }
            }
        }

        return graph
    }

    private fun findChapterByTitle(chapters: List<Chapter>, chapterTitle: String): Chapter? {
        return chapters.firstOrNull { c -> c.title == chapterTitle }
    }
}


