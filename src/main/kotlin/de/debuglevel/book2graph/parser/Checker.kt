package de.debuglevel.book2graph.parser

import de.debuglevel.book2graph.book.Book
import de.debuglevel.book2graph.book.Chapter
import de.debuglevel.book2graph.book.DebugInformationType
import mu.KotlinLogging

class Checker(val book: Book) {
    private val logger = KotlinLogging.logger {}

    fun check() {
        logger.debug { "Checking book for errors..." }
        checkChaptersErrors(book.chapters)
    }

    private fun checkChaptersErrors(chapters: List<Chapter>) {
        logger.debug { "Checking chapters for errors..." }

        for (chapter in chapters) {
            checkChapterErrors(chapters, chapter)
        }
    }

    private fun checkChapterErrors(chapters: List<Chapter>, chapter: Chapter) {
        logger.trace { "Checking chapter for errors..." }

        checkReferences(chapters, chapter)
        checkTitle(chapter)
        checkSummary(chapter)

        logger.trace { "Checked chapter for errors." }
    }

    private fun checkSummary(chapter: Chapter): Boolean {
        logger.trace { "Checking if summary is present in chapter..." }

        val hasSummary = chapter.summary.any { s -> !s.isBlank() }
        if (!hasSummary) {
            logger.info("Chapter '${chapter.title}' has no summary")
            chapter.debugInformation.add(Pair<DebugInformationType, Any?>(DebugInformationType.EmptySummary, null))
        }

        return !hasSummary
    }

    private fun checkTitle(chapter: Chapter): Boolean {
        logger.trace { "Checking if title is present in chapter..." }

        val hasTitle = !chapter.title.isBlank()
        if (!hasTitle) {
            logger.info("Chapter between '${chapter.precedingChapter!!.title}' and '${chapter.succeedingChapter!!.title}' has no name.")
            chapter.debugInformation.add(Pair<DebugInformationType, Any?>(DebugInformationType.EmptyTitle, null))
        }

        return hasTitle
    }

    private fun checkReferences(chapters: List<Chapter>, chapter: Chapter): Boolean {
        var isMissingReferences = false

        for (sibling in chapter.precedingChapterReferences.union(chapter.succeedingChapterReferences)) {
            val exists = chapters.any { c -> c.title == sibling }
            if (!exists) {
                isMissingReferences = true
                logger.info("Chapter '$sibling' is referenced by '${chapter.title}' but does not exist.")
                chapter.debugInformation.add(
                    Pair<DebugInformationType, Any?>(
                        DebugInformationType.MissingReference,
                        sibling
                    )
                )
            }
        }

        return !isMissingReferences
    }
}