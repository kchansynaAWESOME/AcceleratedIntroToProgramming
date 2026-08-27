package LectureStarters

import io.kotest.matchers.shouldBe

/*---------------------------------------*/
/* Objectives:
 * ----------------------------------------
 * 1. Self-referential data: a list is defined in terms of itself
 * 2. Consuming a list with recursion, following the template
 * 3. Producing a list with recursion */

/* ***************************************
 * How to Design Functions (HtDF)
 *    1. Stub (Signature + body/return) + Documentation
 *    2. Example test
 *    3. inventory & template (Work forwards with what we can do with the input we have?)
 *    4. Implement - work backwards from output cases, local variables/helper functions
 *    5. Sufficient Testing and Debugging
 *************************************** */

sealed interface IntList {
    data object Empty : IntList
    data class Node(val first: Int, val rest: IntList) : IntList
}
typealias IEmpty = IntList.Empty
typealias INode = IntList.Node

sealed interface StringList {
    data object Empty : StringList
    data class Node(val first : String, val rest : StringList) : StringList
}

/**
sealed interface MyList<out T> {
    data object Empty : MyList<Nothing>
    data class Node<out T>(
        val first : T,
        val rest  : MyList<T>
    ) : MyList<T>
} */

typealias Sempty = StringList.Empty
typealias Snode = StringList.Node

/* ==========================================
 * Example values
 * ========================================== */

val noNumbers = IEmpty                                   // ()
val oneNumber = INode(42, IEmpty)                        // (42)
val someNumbers = INode(3, INode(1, INode(4, IEmpty)))   // (3 1 4)
val negatives = INode(-7, INode(-2, IEmpty))             // (-7 -2)
val someOtherNumbers =                                   // (3 4 -1 -5)
    INode(3,
        INode(4,
            INode(-1,
                INode(-5, IEmpty))))
val evens = INode(2, INode(4, IEmpty))                   // (2 4)
val odds = INode(3, INode(1, INode(4, IEmpty)))

val noWords = Sempty                                     // ()
val oneWord = Snode("hello", Sempty)                     // ("hello")
val courseName = Snode("cs", Snode("11", Snode("02", Sempty)))  // ("cs" "11" "02")
val courseTitle =                                        // ("Accelerated" "Introduction" "to" "Program" "Design")
    Snode("Accelerated",
        Snode("Introduction",
            Snode("to",
                Snode("Program",
                    Snode("Design", Sempty)))))

/* ==========================================
 * The template
 *
 *   // TODO
 *
 * The data is self-referential (a Node holds an IntList), so the function is self-referential too:
 *    * one branch per case, base case and recursive step.
 *
 * There is no need for an else.  Why not?
 *    * IntList is SEALED.  Empty and Node are the only two ways to build one, and the
 *      compiler knows that.  Cover both branches and you have covered every list there is.
 * ========================================== */

fun main() {
    // Problem - 1
    sum(someNumbers) shouldBe (3+1+4)

    // Problem - 2
    sumAllEven(evens) shouldBe (4+2)


    // Problem - 3
    contains(someNumbers, 4) shouldBe true

    // Problem - 4
    maximum(someOtherNumbers) shouldBe 4

    // Problem - 5
    removeOdds(someNumbers) shouldBe INode(4, IEmpty)

    // Problem - 6
    append(oneNumber, evens) shouldBe INode(42, evens)
    append(oneNumber, evens) shouldBe INode(42, INode(2, INode(4, IEmpty)))

    // Problem - 7
    reverse(someNumbers) shouldBe INode(4, INode(1, INode(3, IEmpty)))

    // Problem - 8
    joinStrings(courseTitle) shouldBe "AcceleratedIntroductiontoProgramDesign"

    // Properties - true for EVERY list
    reverse(reverse(someNumbers)) shouldBe someNumbers
    sum(append(someNumbers, evens)) shouldBe (sum(someNumbers) + sum(evens))
    sum(removeOdds(someNumbers)) shouldBe sumAllEven(someNumbers)
}

/* ==========================================
 * | STUBS
 * ========================================== */

// Problem - 1
/** sum : Adds up every number in the list
 * @param aList any list of numbers, possibly empty
 * @return the total of all the numbers, and 0 for an empty list
 */
fun sum(aList : IntList) : Int {
    // Inventory:  IEmpty  INode  aList.first  aList.rest  sum(...) (recursive call)    +
    return 0 // TODO: Stub - Replace Me
}

// Problem - 2
/** sumAllEven : Adds up only the even numbers in the list
 * @param aList any list of numbers, possibly empty
 * @return the total of the even numbers, and 0 if there are none
 */
fun sumAllEven(aList : IntList) : Int {
    // Inventory: ???
    return 0 // TODO: Stub - Replace Me
}

// Problem - 3
/** contains : Checks whether a number appears anywhere in the list
 * @param aList any list of numbers, possibly empty
 * @param target the number to look for
 * @return true if target appears in the list, false otherwise
 */
fun contains(aList : IntList, target : Int) : Boolean {
    // Inventory: IEmpty  INode  aList.first  aList.rest  target  ==  ||  contains(...)
    return false // TODO: Stub - Replace Me
}

// Problem - 4
/** maximum : Finds the largest number in the list
 * @param aList any list of numbers, possibly empty
 * @return the largest number in the list,
 *         and Int.MIN_VALUE for an empty list (there is no largest number)
 */
fun maximum(aList : IntList) : Int {
    // Inventory: IEmpty  INode  aList.first  aList.rest  maximum(...)  >  if  Int.MIN_VALUE
    return 0 // TODO: Stub - Replace Me
}

// Problem - 5
/** removeOdds : Keeps only the even numbers, in their original order
 * @param aList any list of numbers, possibly empty
 * @return a list of just the even numbers from aList
 */
fun removeOdds(aList : IntList) : IntList {
    // Inventory: ???
    return IEmpty // TODO: Stub - Replace Me
}

// Problem - 6
/** append : Glues two lists together, first list first
 * @param list1 the list whose numbers come first
 * @param list2 the list whose numbers come second
 * @return one list holding all of list1's numbers followed by all of list2's
 */
fun append(list1 : IntList, list2 : IntList) : IntList {
    // Inventory: IEmpty  INode  list1.first  list1.rest  append(...)  INode(...)
    // Hint: only ONE of the two lists needs case analysis.  Which one?
    return IEmpty // TODO: Stub - Replace Me
}

// Problem - 7
/** reverse : Reverses the order of the numbers in the list
 * @param aList any list of numbers, possibly empty
 * @return a list with the same numbers in the opposite order
 */
fun reverse(aList : IntList) : IntList {
    // Inventory: ????
    return IEmpty // TODO: Stub - Replace Me
}

// Problem - 8
/** joinStrings : Joins all the strings in the list together, in order
 * @param aList any list of strings, possibly empty
 * @return one string holding every string in aList, and "" for an empty list
 */
fun joinStrings(aList : StringList) : String {
    // Inventory: ????
    return "" // TODO: Stub - Replace Me
}
