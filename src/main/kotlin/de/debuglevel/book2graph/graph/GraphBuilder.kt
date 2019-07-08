package de.debuglevel.book2graph.graph

import de.debuglevel.book2graph.parser.Chapter
import de.debuglevel.book2graph.parser.RevisionStatus
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
            val time = measureTimeMillis { transitiveReduction(graph) }
            logger.debug { "Removing superseded edges took ${time}ms" }
        }

        logger.debug { "Creating graph done." }
        return graph
    }

    private fun findChapterByTitle(chapters: List<Chapter>, chapterTitle: String): Chapter? {
        return chapters.firstOrNull { c -> c.title == chapterTitle }
    }

    /**
     * Perform an transitive reduction on the graph. All edges which provide shortcuts by bypassing a vertex will be removed. A graph with as few edges as possible is the result.
     * May stuck in a loop or throw an OverflowException if the graph is cyclic.
     */
    private fun transitiveReduction(graph: Graph<Chapter>) {
        logger.debug { "Performing transitive reduction on graph..." }

        for (edge in graph.getEdges()) {
            if (pathExists(edge.start, edge.end, edge)) {
                graph.removeEdge(edge)
                logger.debug { "Removed superseded edge: $edge" }
            }
        }
    }

    private fun pathExists(start: Vertex<Chapter>, end: Vertex<Chapter>, ignoredEdge: Edge<Vertex<Chapter>>): Boolean {
        return findVertex(start, end, ignoredEdge)
    }

    /**
     * Walk tree (starting from "start") to find vertex "breakingCondition".
     */
    private fun findVertex(
        start: Vertex<Chapter>,
        breakingCondition: Vertex<Chapter>,
        ignoredEdge: Edge<Vertex<Chapter>>?
    ): Boolean {
        val descendants =
            when {
                // only filter if ignoredEdge is not null (i.e. we are on the first level of the recursive tree). Saved about 50% time.
                ignoredEdge != null -> start.outEdges
                    .filter { it !== ignoredEdge }
                    .map { it.end }
                else -> start.outEdges
                    .map { it.end }
            }

        if (descendants.contains(breakingCondition)) {
            return true
        }

        for (descendant in descendants) {
            // ignoredEdge is replaced by null here, as it is only relevant in the first call level of the recursion
            if (findVertex(descendant, breakingCondition, null)) {
                return true
            }
        }

        return false
    }
}


