package de.debuglevel.book2graph.graph

import de.debuglevel.book2graph.book.Chapter
import de.debuglevel.book2graph.book.RevisionStatus
import mu.KotlinLogging
import kotlin.system.measureTimeMillis

object GraphBuilder {
    private val logger = KotlinLogging.logger {}

    fun build(
        chapters: List<Chapter>,
        transitiveReduction: Boolean
    ): Graph<Chapter> {
        logger.debug { "Creating graph..." }

        val graph = Graph<Chapter>()

        val chapterVertices = mutableListOf<Vertex<Chapter>>()
        for (chapter in chapters) {
            val color = when {
                chapter.revisionStatus == RevisionStatus.Good -> Color.Green
                chapter.revisionStatus == RevisionStatus.Improvable -> Color.Red
                chapter.revisionStatus == RevisionStatus.NotReviewed -> Color.Yellow
                chapter.revisionStatus == RevisionStatus.Milestone -> Color.Blue
                chapter.revisionStatus == RevisionStatus.Unknown -> Color.Gray
                else -> Color.Gray
            }

            val shape = when {
                chapter.revisionStatus == RevisionStatus.Milestone -> Shape.Rectangle
                else -> Shape.Ellipse
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

        if (transitiveReduction) {
            val duration = measureTimeMillis { GraphUtils.transitiveReduction(graph) }
            logger.debug { "Removing superseded edges took ${duration}ms" }
        }

        logger.debug { "Creating graph done." }
        return graph
    }

    private fun findChapterByTitle(chapters: List<Chapter>, chapterTitle: String): Chapter? {
        return chapters.firstOrNull { c -> c.title == chapterTitle }
    }
}


