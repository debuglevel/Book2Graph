//
// Translated by CS2J (http://www.cs2j.com): 24.08.2018 23:51:07
//

package Book2Chart.Parser

import Book2Chart.Parser.Chapter
import Book2Chart.Parser.DebugInformationType
import Book2Chart.Parser.RevisionStatus

class Chapter @Throws(Exception::class)
constructor() {
    var title = String()

    var text = List<String>()

    var comment = List<String>()

    var summary = List<String>()

    var precedingChapterReferences = List<String>()

    var succeedingChapterReferences = List<String>()

    var precedingChapter: Chapter? = null

    var succeedingChapter: Chapter? = null

    var debugInformation = List<KeyValuePair<DebugInformationType, Any>>()

    var revisionStatus = RevisionStatus.Unknown

    val textAsString: String
        @Throws(Exception::class)
        get() = String.Join(Environment.NewLine, this.text)

    val commentAsString: String
        @Throws(Exception::class)
        get() = String.Join(Environment.NewLine, this.comment)

    val summaryAsString: String
        @Throws(Exception::class)
        get() = String.Join(Environment.NewLine, this.summary)

    init {
        this.comment = List()
        this.summary = List()
        this.text = List()
        this.precedingChapterReferences = List()
        this.succeedingChapterReferences = List()
        this.debugInformation = List<KeyValuePair<DebugInformationType, Any>>()
    }

}


