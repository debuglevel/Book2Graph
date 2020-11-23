package de.debuglevel.book2graph.graph

import de.debuglevel.book2graph.book.Chapter
import de.debuglevel.book2graph.book.RevisionStatus
import de.debuglevel.graphlibrary.Color
import de.debuglevel.graphlibrary.NodeInformationRetriever
import de.debuglevel.graphlibrary.Shape
import de.debuglevel.graphlibrary.Vertex
import mu.KotlinLogging

class ChapterNodeInformationRetriever : NodeInformationRetriever<Chapter> {
    private val logger = KotlinLogging.logger {}

    private fun findChapterByTitle(chapters: List<Chapter>, chapterTitle: String): Chapter? {
        return chapters.firstOrNull { it.title == chapterTitle }
    }

    override fun getColor(node: Chapter): Color {
        return when (node.revisionStatus) {
            RevisionStatus.Good -> Color.Green
            RevisionStatus.Improvable -> Color.Red
            RevisionStatus.NotReviewed -> Color.Yellow
            RevisionStatus.Milestone -> Color.Blue
            RevisionStatus.Unknown -> Color.Gray
            else -> Color.Gray
        }
    }

    override fun getShape(node: Chapter): Shape {
        return when (node.revisionStatus) {
            RevisionStatus.Milestone -> Shape.Rectangle
            else -> Shape.Ellipse
        }
    }

    override fun getTooltip(node: Chapter): String {
        return node.summaryAsString.replace("\n", "&#10;")
    }

    override fun getPrecedingVertices(
        vertex: Vertex<Chapter>,
        allVertices: List<Vertex<Chapter>>
    ): List<Vertex<Chapter>> {
        val allChapters = allVertices.map { it.content }

        return vertex.content.precedingChapterReferences.mapNotNull { precedingChapterString ->
            val precedingChapter = this.findChapterByTitle(allChapters, precedingChapterString)
            allVertices.firstOrNull { it.content == precedingChapter }
        }
    }

    override fun getSucceedingVertices(
        vertex: Vertex<Chapter>,
        allVertices: List<Vertex<Chapter>>
    ): List<Vertex<Chapter>> {
        val allChapters = allVertices.map { it.content }

        return vertex.content.succeedingChapterReferences.mapNotNull { succeedingChapterString ->
            val succeedingChapter = this.findChapterByTitle(allChapters, succeedingChapterString)
            allVertices.firstOrNull { it.content == succeedingChapter }
        }
    }
}


