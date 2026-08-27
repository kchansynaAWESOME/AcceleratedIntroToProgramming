//Kingston Chansyna, Kiera Winters

package Labs
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.math.min

/** a Datatype for an author
 * @property firstName the author's first name
 * @property lastName the author's last name
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

// Consumes a piece of text and returns the text after Diff is applied
// @param initText Initial text before Diff is applied
// @param diff Diff object that applied to the given text
// @return The initial text after the diff is applied
// @throw IndexOutOfBoundsException if the Diff's position is invalid
// @throw NoSuchElementException if the Delete Diff cannot find the the text2Delete at the given position
//fun applyDiff(initText:String,diff:Diff) : String {
//    return "" //STUB --TODO
//}

/* ==========================================
 * | TESTS
 * ========================================== */
fun main() {
    applyDiff("I LOVE THIS",Diff.Insert(1," DONT")) shouldBe "I DONT LOVE THIS"
    shouldThrow<IndexOutOfBoundsException> {applyDiff("",Diff.Insert(1,""))}
    shouldThrow<IndexOutOfBoundsException> {applyDiff("I LOVE THIS",Diff.Insert(-20," DONT"))}
    applyDiff("I REALLY LOVE THIS",Diff.Delete(1," REALLY",true)) shouldBe "I LOVE THIS"
    applyDiff("I REALLY LOVE THIS",Diff.Delete(1," REALLY",false)) shouldBe "I        LOVE THIS"
    shouldThrow<NoSuchElementException> {applyDiff("THIS IS THE BEST",Diff.Delete(1," REALLY",true))}
    applyDiff("hi",Diff.Delete(0,"hi",true)) shouldBe ""
}

/* ==========================================
 * | Part - 1
 * ========================================== */

// Task - 1 : applyDiff

// Consumes a piece of text and returns the text after Diff is applied
// @param initText Initial text before Diff is applied
// @param diff Diff object that applied to the given text
// @return The initial text after the diff is applied
// @throw IndexOutOfBoundsException if the Diff's position is invalid
// @throw NoSuchElementException if the Delete Diff cannot find the the text2Delete at the given position
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
    if (initText.substring(diff.position, deleteEnd) != diff.text2Delete) throw NoSuchElementException("no")

    val replacement = if (!diff.shouldCondense) " ".repeat(diff.text2Delete.length) else ""
    return initText.substring(0, diff.position) + replacement + initText.substring(deleteEnd)
}

// you dont wanna know
fun textOccurs(mainText:String,checkText:String) : String {
    return ""
}


// Task - 2 : undoDiff

// Task - 3 : applyAllDiffs

/* ==========================================
 * | Part - 2
 * ========================================== */

// Task - 4 : redo

// Task - 5 : undo

// Task - 6 : update

// Task - 7 : isLargerThan

// Task - 8 : hasTag
