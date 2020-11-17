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

        val chapterVertices = chapters.map { chapter ->
            val color = when (chapter.revisionStatus) {
                RevisionStatus.Good -> Color.Green
                RevisionStatus.Improvable -> Color.Red
                RevisionStatus.NotReviewed -> Color.Yellow
                RevisionStatus.Milestone -> Color.Blue
                RevisionStatus.Unknown -> Color.Gray
                else -> Color.Gray
            }

            val shape = when (chapter.revisionStatus) {
                RevisionStatus.Milestone -> Shape.Rectangle
                else -> Shape.Ellipse
            }

            val tooltip = chapter.summaryAsString.replace("\n", "&#10;")

            val vertex = Vertex(chapter, color, shape, tooltip)
            graph.addVertex(vertex)
            vertex
        }

        for (chapterVertex in chapterVertices) {
            for (precedingChapterString in chapterVertex.vertex.precedingChapterReferences) {
                val precedingChapter = this.findChapterByTitle(chapters, precedingChapterString)
                chapterVertices.firstOrNull { it.vertex == precedingChapter }?.let { precedingChapterVertex ->
                    graph.addEdge(Edge(precedingChapterVertex, chapterVertex))
                }
            }

            for (succeedingChapterString in chapterVertex.vertex.succeedingChapterReferences) {
                val succeedingChapter = this.findChapterByTitle(chapters, succeedingChapterString)
                chapterVertices.firstOrNull { it.vertex == succeedingChapter }?.let { succeedingChapterVertex ->
                    graph.addEdge(Edge(chapterVertex, succeedingChapterVertex))
                }
            }
        }

        if (transitiveReduction) {
            val duration = measureTimeMillis { TransitiveReduction.reduce(graph) }
            logger.debug { "Removing superseded edges took ${duration}ms" }
        }

        logger.debug { "Created graph" }
        return graph
    }

    private fun findChapterByTitle(chapters: List<Chapter>, chapterTitle: String): Chapter? {
        return chapters.firstOrNull { it.title == chapterTitle }
    }
}


