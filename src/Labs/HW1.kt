//Kingston Chansyna, Kiera Winters

package Labs
import LectureStarters.SNode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.math.min
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** a Datatype for an author
 * @property firstName the author's first name
 * @property lavstName the author's last name
 * @property email the author's email address
 * @property organization the organization the author belongs to
 */
data class Author (val firstName:String,
                   val lastName:String,
                   val email:String,val
                   organization:String)

/** a Datatype for a timestamp
 * @property day day of the month (1-31)
 * @property month month of the year (1-12)
 * @property year year in AD (>= 0)
 * @property hour hour of the day in 24-hour time (0-23)
 * @property minute minute of the hour (0-59)
 * @property second second of the minute (0-59)
 */
data class Timestamp(val day:Int,
                     val month:Int,
                     val year:Int,
                     val hour:Int,
                     val minute:Int,
                     val second:Int)



/** Sum type for a document text update */
sealed interface Diff {
    /** a Datatype for an insertion edit
     * @property position the index in the text where new text should be inserted
     * @property text2Insert the text to insert at the given position
     */
    data class Insert(val position:Int,val text2Insert:String) : Diff

    /** a Datatype for a deletion edit
     * @property position the index in the text where deletion should begin
     * @property text2Delete the exact substring expected to be deleted at that position
     * @property shouldCondense true to remove the substring, false to replace it with same-length spaces
     */
    data class Delete(val position:Int,val text2Delete:String,val shouldCondense:Boolean) : Diff
}

// Type aliases
typealias Insert = Diff.Insert
typealias Delete = Diff.Delete

/** a Datatype for a document
 * @property author the author of the document
 * @property created the timestamp of when the document was first created
 * @property updated the timestamp of the most recent edit to the document
 * @property title the title of the document
 * @property text the current text of the document
 * @property history the stack of applied edits, most recent first (undo stack)
 * @property future the stack of undone edits, most recently undone first (redo stack)
 * @property tags the list of tags associated with the document
 */
data class Document (val author : Author,
                     val created : Timestamp,
                     val updated: Timestamp,
                     val title : String,
                     val text : String,
                     val history : DiffList,
                     val future : DiffList,
                     val tags : StringList
)
/* ==========================================
 * | STUBS
 * ========================================== */

/** Consumes a piece of text and returns the text after Diff is applied
 * @param initText Initial text before Diff is applied
 * @param diff Diff object that applied to the given text
 * @return The initial text after the diff is applied
 * @throw IndexOutOfBoundsException if the Diff's position is invalid
 * @throw NoSuchElementException if the Delete Diff cannot find the the text2Delete at the given position
 */
//fun applyDiff(initText:String,diff:Diff) : String {
//    return "" //STUB
//}

/** undoDiff: consumes text that was affected by a Diff and "undoes" it in a sense.
 * Should only consume the diff that was applied to the text beforehand, and should always return the unaffected text
 * @param text the text after the diff was applied
 * @param diff the diff object applied to the text beforehand
 * @return the original text before the diff was applied
 */
//fun undoDiff(text:String,diff: Diff) : String {
//    return "" // STUB
//}

/** applyAllDiffs: consumes raw text and a [DiffList] and applies every diff in the list to the text
 * @param text
 * @param diffList
 * @return final text with all diffs applied
 */
//fun applyAllDiffs(text:String,diffList: DiffList) : String {
//    return "" // STUB
//}

/** redo: consumes a [Document] and reapplies the most recent undone edit.
 * @param document the [Document] object
 * @return a [Document] object with the undone edit applied
 * @exception NoSuchElementException if there are no previous undos in the document's history
 */
//fun redo(document:Document) : Document {
//    return document // STUB
//}

/** undo: consumes a [Document] and reverses a previously applied edit.
 * @param document
 * @return a [Document] object with a previous edit reversed
 * @exception NoSuchElementException if there have been no previous edits to the provided document
 */
//fun undo(document:Document) : Document {
//    return document // STUB
//}

/** update: consumes a [Document] and a [Diff] and applies a new, fresh edit.
 * Due to this being a new edit, any previous history and future on the document are cleared.
 * @param document the [Document] that will bear the new edit
 * @param diff the [Diff] edit that will be applied to the provided [Document]
 * @return a [Document] with the new edit applied
 */
//fun update(document:Document,diff:Diff) : Document {
//    return document // STUB
//}

/** isLargerThan: consumes two [Document]s and returns true when the first document's text is strictly longer than the second's
 * Returns false when otherwise, including when the two are the same length
 * @param doc1 the first [Document] being compared
 * @param doc2 the second [Document] being compared
 * @return true if the first [Document] is larger than the second, false if not (or if they have the same size)
 */
//fun isLargerThan(doc1:Document,doc2:Document) : Boolean {
//    return false // STUB
//}

/** hasTag: consumes a [Document] and a tag, and returns true if that tag appears anywhere in the document's tags list
 * @param document the [Document] being checked for a given tag
 * @param tag the tag being checked over [document]
 * @return true if the provided [tag] appears in [document]'s tag list, and false otherwise
 */
//fun hasTag(document:Document,tag:String) : Boolean {
//    return false // STUB
//}

/* ==========================================
 * | TESTS
 * ========================================== */

// runs several tests for every function in this script
fun runTests() {
    applyDiff("I LOVE THIS",Insert(1," DONT")) shouldBe "I DONT LOVE THIS"
    shouldThrow<IndexOutOfBoundsException> {applyDiff("",Insert(1,""))}
    shouldThrow<IndexOutOfBoundsException> {applyDiff("I LOVE THIS",Insert(-20," DONT"))}
    applyDiff("I REALLY LOVE THIS",Delete(1," REALLY",true)) shouldBe "I LOVE THIS"
    applyDiff("I REALLY LOVE THIS",Delete(1," REALLY",false)) shouldBe "I        LOVE THIS"
    shouldThrow<NoSuchElementException> {applyDiff("THIS IS THE BEST",Delete(1," REALLY",true))}
    applyDiff("hi",Delete(0,"hi",true)) shouldBe ""

    undoDiff("I DONT LOVE THIS",Insert(1," DONT")) shouldBe "I LOVE THIS"
    undoDiff("I",Insert(0,"I")) shouldBe ""
    undoDiff("I LOVE THIS", Delete(1," DONT",true)) shouldBe "I DONT LOVE THIS"
    undoDiff("", Delete(0,"DONT",true)) shouldBe "DONT"

    applyAllDiffs("attention all students i hate my job",
        Dnode(
            Insert(24," really"), Dnode(
                Insert(31,", really"),Dempty))) shouldBe "attention all students i really, really hate my job"
    applyAllDiffs("I LOVE THIS", Dempty) shouldBe "I LOVE THIS"
    applyAllDiffs("I LOVE THIS", Dnode(
        Insert(1," DONT"), Dempty)) shouldBe "I DONT LOVE THIS"
    applyAllDiffs("I REALLY LOVE THIS", Dnode(
        Delete(1," REALLY",true), Dempty)) shouldBe "I LOVE THIS"
    applyAllDiffs("I REALLY LOVE THIS", Dnode(
        Delete(1," REALLY",false), Dempty)) shouldBe "I        LOVE THIS"
    applyAllDiffs("I LOVE THIS", Dnode(
        Insert(1," REALLY"), Dnode(
            Delete(2,"REALLY",true), Dnode(
                Insert(1," DONT"), Dempty)))) shouldBe "I DONT  LOVE THIS"
    shouldThrow<NoSuchElementException> {
        applyAllDiffs("I LOVE THIS", Dnode(
            Insert(1," DONT"), Dnode(
                Delete(1," REALLY",true), Dempty)))
    }
    shouldThrow<IndexOutOfBoundsException> {
        applyAllDiffs("I LOVE THIS", Dnode(
            Insert(1," DONT"), Dnode(
                Insert(100,"!"), Dempty)))
    }
    applyAllDiffs("hi", Dnode(
        Delete(0,"hi",true), Dnode(
            Insert(0,"bye"), Dempty))) shouldBe "bye"

    // redo edge cases
    val testAuthor = Author("John","Doe","john@doe.com","Org")
    val testCreated = Timestamp(1,1,2024,0,0,0)
    val testUpdated = Timestamp(2,1,2024,12,0,0)

    // empty future: nothing to redo
    shouldThrow<NoSuchElementException> {
        redo(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
            Dempty, Dempty, Sempty))
    }

    // single diff in future: applies it, future becomes empty, diff moves to history
    val redoSingle = redo(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
        Dempty, Dnode(Insert(1," DONT"), Dempty), Sempty))
    redoSingle.text shouldBe "I DONT LOVE THIS"
    redoSingle.future shouldBe Dempty
    redoSingle.history shouldBe Dnode(Insert(1," DONT"), Dempty)
    redoSingle.author shouldBe testAuthor
    redoSingle.created shouldBe testCreated
    redoSingle.title shouldBe "Title"
    redoSingle.tags shouldBe Sempty
    redoSingle.updated shouldNotBe testUpdated

    // multiple diffs in future: only the first is applied, the rest stays in future,
    // and it's pushed on top of the existing history
    val redoMultiple = redo(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
        Dnode(Delete(0,"OLD",true), Dempty),
        Dnode(Insert(1," DONT"), Dnode(Insert(0,"X"), Dempty)), Sempty))
    redoMultiple.text shouldBe "I DONT LOVE THIS"
    redoMultiple.future shouldBe Dnode(Insert(0,"X"), Dempty)
    redoMultiple.history shouldBe Dnode(Insert(1," DONT"), Dnode(Delete(0,"OLD",true), Dempty))

    // redo on an empty document text
    redo(Document(testAuthor, testCreated, testUpdated, "Title", "",
        Dempty, Dnode(Insert(0,"hi"), Dempty), Sempty)).text shouldBe "hi"

    // an out-of-bounds diff at the front of future propagates IndexOutOfBoundsException
    shouldThrow<IndexOutOfBoundsException> {
        redo(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
            Dempty, Dnode(Insert(100,"!"), Dempty), Sempty))
    }

    // a Delete diff whose text2Delete isn't actually present propagates NoSuchElementException
    shouldThrow<NoSuchElementException> {
        redo(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
            Dempty, Dnode(Delete(0,"NOTPRESENT",true), Dempty), Sempty))
    }

    // undo edge cases

    // empty history: nothing to undo
    shouldThrow<NoSuchElementException> {
        undo(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
            Dempty, Dempty, Sempty))
    }

    // single diff in history: undoes it, history becomes empty, diff moves to future
    val undoSingle = undo(Document(testAuthor, testCreated, testUpdated, "Title", "I DONT LOVE THIS",
        Dnode(Insert(1," DONT"), Dempty), Dempty, Sempty))
    undoSingle.text shouldBe "I LOVE THIS"
    undoSingle.history shouldBe Dempty
    undoSingle.future shouldBe Dnode(Insert(1," DONT"), Dempty)
    undoSingle.author shouldBe testAuthor
    undoSingle.created shouldBe testCreated
    undoSingle.title shouldBe "Title"
    undoSingle.tags shouldBe Sempty
    undoSingle.updated shouldNotBe testUpdated

    // multiple diffs in history: only the top one is undone, the rest stays in history,
    // and it's pushed on top of the existing future
    val undoMultiple = undo(Document(testAuthor, testCreated, testUpdated, "Title", "I DONT LOVE THIS",
        Dnode(Insert(1," DONT"), Dnode(Insert(0,"X"), Dempty)),
        Dnode(Delete(0,"OLD",true), Dempty), Sempty))
    undoMultiple.text shouldBe "I LOVE THIS"
    undoMultiple.history shouldBe Dnode(Insert(0,"X"), Dempty)
    undoMultiple.future shouldBe Dnode(Insert(1," DONT"), Dnode(Delete(0,"OLD",true), Dempty))

    // undoing a Delete diff re-inserts the deleted text
    val undoDelete = undo(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
        Dnode(Delete(1," REALLY",true), Dempty), Dempty, Sempty))
    undoDelete.text shouldBe "I REALLY LOVE THIS"
    undoDelete.future shouldBe Dnode(Delete(1," REALLY",true), Dempty)

    // undoing down to an empty string
    undo(Document(testAuthor, testCreated, testUpdated, "Title", "hi",
        Dnode(Insert(0,"hi"), Dempty), Dempty, Sempty)).text shouldBe ""

    // history's top diff no longer matches the current text -> NoSuchElementException propagates
    shouldThrow<NoSuchElementException> {
        undo(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
            Dnode(Insert(1," DONT"), Dempty), Dempty, Sempty))
    }

    // history's top diff position is out of bounds for the current text -> IndexOutOfBoundsException propagates
    shouldThrow<IndexOutOfBoundsException> {
        undo(Document(testAuthor, testCreated, testUpdated, "Title", "hi",
            Dnode(Insert(100,"!"), Dempty), Dempty, Sempty))
    }

    // update edge cases

    // update from a clean document: applies the diff, and history becomes a single-entry list of it
    val updateFromClean = update(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
        Dempty, Dempty, Sempty), Insert(1," DONT"))
    updateFromClean.text shouldBe "I DONT LOVE THIS"
    updateFromClean.history shouldBe Dnode(Insert(1," DONT"), Dempty)
    updateFromClean.future shouldBe Dempty
    updateFromClean.author shouldBe testAuthor
    updateFromClean.created shouldBe testCreated
    updateFromClean.title shouldBe "Title"
    updateFromClean.tags shouldBe Sempty
    updateFromClean.updated shouldNotBe testUpdated

    // update wipes out pre-existing history and future entirely, rather than pushing onto them
    val updateFromDirty = update(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
        Dnode(Delete(0,"OLD",true), Dempty),
        Dnode(Insert(0,"X"), Dempty), Sempty), Insert(1," DONT"))
    updateFromDirty.history shouldBe Dnode(Insert(1," DONT"), Dempty)
    updateFromDirty.future shouldBe Dempty

    // update with a Delete diff
    val updateWithDelete = update(Document(testAuthor, testCreated, testUpdated, "Title", "I REALLY LOVE THIS",
        Dempty, Dempty, Sempty), Delete(1," REALLY",true))
    updateWithDelete.text shouldBe "I LOVE THIS"
    updateWithDelete.history shouldBe Dnode(Delete(1," REALLY",true), Dempty)

    // update down to an empty string
    update(Document(testAuthor, testCreated, testUpdated, "Title", "hi",
        Dempty, Dempty, Sempty), Delete(0,"hi",true)).text shouldBe ""

    // an out-of-bounds diff position propagates IndexOutOfBoundsException
    shouldThrow<IndexOutOfBoundsException> {
        update(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
            Dempty, Dempty, Sempty), Insert(100,"!"))
    }

    // a Delete diff whose text2Delete isn't actually present propagates NoSuchElementException
    shouldThrow<NoSuchElementException> {
        update(Document(testAuthor, testCreated, testUpdated, "Title", "I LOVE THIS",
            Dempty, Dempty, Sempty), Delete(0,"NOTPRESENT",true))
    }

    // isLargerThan edge cases
    val shortDoc = Document(testAuthor, testCreated, testUpdated, "Title", "hi", Dempty, Dempty, Sempty)
    val otherShortDoc = Document(testAuthor, testCreated, testUpdated, "Title", "yo", Dempty, Dempty, Sempty)
    val longDoc = Document(testAuthor, testCreated, testUpdated, "Title", "hello there", Dempty, Dempty, Sempty)
    val emptyDoc = Document(testAuthor, testCreated, testUpdated, "Title", "", Dempty, Dempty, Sempty)

    isLargerThan(longDoc, shortDoc) shouldBe true
    isLargerThan(shortDoc, longDoc) shouldBe false
    // equal length but different text still counts as not larger
    isLargerThan(shortDoc, otherShortDoc) shouldBe false
    // a document is never larger than an identical copy of itself
    isLargerThan(shortDoc, shortDoc) shouldBe false
    // comparing against an empty document
    isLargerThan(shortDoc, emptyDoc) shouldBe true
    isLargerThan(emptyDoc, shortDoc) shouldBe false
    // two empty documents are not larger than each other
    isLargerThan(emptyDoc, emptyDoc) shouldBe false

    // hasTag edge cases
    val noTagsDoc = Document(testAuthor, testCreated, testUpdated, "Title", "text", Dempty, Dempty, Sempty)
    val oneTagDoc = Document(testAuthor, testCreated, testUpdated, "Title", "text", Dempty, Dempty,
        Snode("kotlin", Sempty))
    val manyTagsDoc = Document(testAuthor, testCreated, testUpdated, "Title", "text", Dempty, Dempty,
        Snode("first", Snode("middle", Snode("last", Sempty))))
    val dupeTagsDoc = Document(testAuthor, testCreated, testUpdated, "Title", "text", Dempty, Dempty,
        Snode("dup", Snode("dup", Sempty)))

    // empty tag list never has any tag, including the empty string
    hasTag(noTagsDoc, "kotlin") shouldBe false
    hasTag(noTagsDoc, "") shouldBe false

    // single-tag list, matching and non-matching
    hasTag(oneTagDoc, "kotlin") shouldBe true
    hasTag(oneTagDoc, "java") shouldBe false

    // tag located at the front, middle, and end of a multi-entry list
    hasTag(manyTagsDoc, "first") shouldBe true
    hasTag(manyTagsDoc, "middle") shouldBe true
    hasTag(manyTagsDoc, "last") shouldBe true
    hasTag(manyTagsDoc, "nope") shouldBe false

    // tag matching is case-sensitive
    hasTag(oneTagDoc, "Kotlin") shouldBe false

    // duplicate entries still resolve to a single true
    hasTag(dupeTagsDoc, "dup") shouldBe true
}

fun main() {
    runTests()
}

/* ==========================================
 * | Part - 1
 * ========================================== */

// Task - 1 : applyDiff

/** Consumes a piece of text and returns the text after Diff is applied
 * @param initText Initial text before Diff is applied
 * @param diff Diff object that applied to the given text
 * @return The initial text after the diff is applied
 * @throw IndexOutOfBoundsException if the Diff's position is invalid
 * @throw NoSuchElementException if the Delete Diff cannot find the the text2Delete at the given position
 */
fun applyDiff(initText:String,diff:Diff) : String {
    return when (diff) {
        is Insert -> applyInsert(initText, diff)
        is Delete -> applyDelete(initText, diff)
    }
}

fun applyInsert(initText: String, diff: Insert): String {
    if (diff.position < 0 || diff.position > initText.length) throw IndexOutOfBoundsException("out of bounds")
    return initText.substring(0, diff.position) + diff.text2Insert + initText.substring(diff.position)
}

fun applyDelete(initText: String, diff: Delete): String {
    if (diff.position < 0 || diff.position > initText.length) throw IndexOutOfBoundsException("out of bounds")

    val deleteEnd = min(diff.position + diff.text2Delete.length, initText.length)
    if (initText.substring(diff.position, deleteEnd) != diff.text2Delete) throw NoSuchElementException("could not find text to delete")

    val replacement = if (!diff.shouldCondense) " ".repeat(diff.text2Delete.length) else ""
    return initText.substring(0, diff.position) + replacement + initText.substring(deleteEnd)
}


// Task - 2 : undoDiff

/** undoDiff: consumes text that was affected by a Diff and "undoes" it in a sense.
 * Should only consume the diff that was applied to the text beforehand, and should always return the unaffected text
 * @param text the text after the diff was applied
 * @param diff the diff object applied to the text beforehand
 * @return the original text before [diff] was applied
 */
fun undoDiff(text:String,diff: Diff) : String {
    return when (diff) {
        is Insert -> applyDelete(text,Delete(diff.position,diff.text2Insert,true))
        is Delete -> applyInsert(text,Insert(diff.position,diff.text2Delete))
    }
}

// Task - 3 : applyAllDiffs

/** applyAllDiffs: consumes raw text and a [DiffList] and applies every diff in the list to the text
 * @param text
 * @param diffList
 * @return final text with all diffs applied
 */
fun applyAllDiffs(text:String,diffList: DiffList) : String {
    return when (diffList) {
        is Dempty -> text
        is Dnode -> {
            when (diffList.first) {
                is Insert -> applyAllDiffs(applyInsert(text,diffList.first),diffList.rest)
                is Delete -> applyAllDiffs(applyDelete(text,diffList.first),diffList.rest)
            }
        }
    }
}

/* ==========================================
 * | Part - 2
 * ========================================== */

// Task - 4 : redo

fun currentTime() : Timestamp {
    val current = LocalDateTime.now()

    return Timestamp(
        current.dayOfMonth,
        current.monthValue,
        current.year,
        current.hour,
        current.minute,
        current.second
    )
}

/** redo: consumes a [Document] and reapplies the most recent undone edit.
 * @param document the [Document] object
 * @return a [Document] object with the undone edit applied
 * @exception NoSuchElementException if there are no previous undos in the document's history
 */
fun redo(document:Document) : Document {
    return Document(
        document.author,
        document.created,
        currentTime(),
        document.title,
        undoDiff(document.text,getFirst(document.history)),
        Dnode(getFirst(document.future),document.history),
        if (document.future is Dnode) document.future.rest else Dempty,
        document.tags

    )
}

// Task - 5 : undo

/** undo: consumes a [Document] and reverses a previously applied edit.
 * @param document
 * @return a [Document] object with a previous edit reversed
 * @exception NoSuchElementException if there have been no previous edits to the provided document
 */
fun undo(document:Document) : Document {
    return Document(
        document.author,
        document.created,
        currentTime(),
        document.title,
        applyDiff(document.text,getFirst(document.future)),
        if (document.history is Dnode) document.history.rest else Dempty,
        Dnode(getFirst(document.history),document.future),
        document.tags
    )
}

// Task - 6 : update

/** update: consumes a [Document] and a [Diff] and applies a new, fresh edit.
 * Due to this being a new edit, any previous history and future on the document are cleared.
 * @param document the [Document] that will bear the new edit
 * @param diff the [Diff] edit that will be applied to the provided [Document]
 * @return a [Document] with the new edit applied
 */
fun update(document:Document,diff:Diff) : Document {
    return Document(
        document.author,
        document.created,
        currentTime(),
        document.title,
        applyDiff(document.text,diff),
        Dnode(diff, Dempty),
        Dempty,
        document.tags
    )
}

// Task - 7 : isLargerThan

/** isLargerThan: consumes two [Document]s and returns true when the first document's text is strictly longer than the second's
 * Returns false when otherwise, including when the two are the same length
 * @param doc1 the first [Document] being compared
 * @param doc2 the second [Document] being compared
 * @return true if [doc1] is larger than [doc2], false if not (or if they have the same size)
 */
fun isLargerThan(doc1:Document,doc2:Document) : Boolean {
    return doc1.text.length > doc2.text.length
}

// Task - 8 : hasTag

/** hasTag: consumes a [Document] and a tag, and returns true if that tag appears anywhere in the document's tags list
 * @param document the [Document] being checked for a given tag
 * @param tag the tag being checked over [document]
 * @return true if the provided [tag] appears in [document]'s tag list, and false otherwise
 */
fun hasTag(document:Document,tag:String) : Boolean {
    return getStrInList(document.tags,tag)
}

/** getStrInList: recurses over a provided [StringList] and checks whether [str] appears at least once inside it
 * @param strList the [StringList] being searched
 * @param str the [String] that is being searched for inside of [strList]
 * @return true if [str] was found inside [strList] and false otherwise
 */
private fun getStrInList(strList: StringList,str : String) : Boolean {
    return when (strList) {
        is Sempty -> return false
        is Snode -> (strList.first == str) || getStrInList(strList.rest, str)
    }
}
