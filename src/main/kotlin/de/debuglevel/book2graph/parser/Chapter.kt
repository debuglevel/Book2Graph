package de.debuglevel.book2graph.parser

class Chapter {
    var title = ""
    var text = mutableListOf<String>()
    var comment = mutableListOf<String>()
    var summary = mutableListOf<String>()
    var precedingChapterReferences = mutableListOf<String>()
    var succeedingChapterReferences = mutableListOf<String>()
    var precedingChapter: Chapter? = null
    var succeedingChapter: Chapter? = null
    var debugInformation = mutableListOf<Pair<DebugInformationType, Any?>>()
    var revisionStatus = RevisionStatus.Unknown

    val textAsString =
            this.text.joinToString("\n")

    val commentAsString = this.comment.joinToString("\n")

    val summaryAsString = this.summary.joinToString("\n")

    override fun toString(): String {
        return title
    }
}


