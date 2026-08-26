import Labs.Diff

/**
 * CS 1102 - HW1
 * Sakire Arslan Ay
 * Starter code: linked list definitions and helpers.
 *
 * Given here:
 *  - `StringList` (Empty / Node) for lists of tags
 *  - `DiffList` (Empty / Node) for lists of diffs
 *  - helper functions on `DiffList`: getFirst, addFirst, removeFirst, getLast, removeLast
 *
 * Do NOT change the data definitions in this file. Write your solutions in HW1.kt.
 */

/** Sum type for a linked list of Strings (used for a document's tags) */
sealed interface StringList {
    /** the empty list */
    data object Empty : StringList
    /** a non-empty list
     * @property first the String at the front of the list
     * @property rest the remaining Strings in the list
     */
    data class Node(val first : String, val rest : StringList) : StringList
}

typealias Sempty = StringList.Empty
typealias Snode = StringList.Node

/** Sum type for a linked list of Diffs (used for a document's history and future) */
sealed interface DiffList {
    /** the empty list */
    data object Empty : DiffList
    /** a non-empty list
     * @property first the Diff at the front of the list
     * @property rest the remaining Diffs in the list
     */
    data class Node(val first : Diff, val rest : DiffList) : DiffList
}
typealias Dempty = DiffList.Empty
typealias Dnode = DiffList.Node


/**
 * Returns the first Diff in the list.
 * @param diffLst the list to look at
 * @return the Diff at the front of the list
 * @throws NoSuchElementException if the list is empty
 */
fun getFirst (diffLst : DiffList): Diff {
    return when (diffLst) {
        is Dempty -> throw NoSuchElementException("List is empty!")
        is Dnode ->  { diffLst.first }
    }
}

/**
 * Adds a Diff to the front of the list.
 * @param diff the Diff to add
 * @param diffLst the list to add to
 * @return a new list with `diff` at the front, followed by `diffLst`
 */
fun addFirst (diff: Diff, diffLst : DiffList): DiffList {
    return Dnode(diff, diffLst)
}

/**
 * Removes the first Diff from the list.
 * @param diffLst the list to remove from
 * @return the list without its first Diff, or the empty list if `diffLst` is empty
 */
fun removeFirst (diffLst : DiffList): DiffList {
    return when (diffLst) {
        is Dempty -> Dempty
        is Dnode ->  { diffLst.rest }
    }
}

/**
 * Returns the last Diff in the list.
 * @param diffLst the list to look at
 * @return the Diff at the end of the list
 * @throws NoSuchElementException if the list is empty
 */
fun getLast (diffLst : DiffList): Diff {
    return when (diffLst) {
        is Dempty -> throw NoSuchElementException("List is empty!")
        is Dnode -> if (diffLst.rest is Dempty)  { diffLst.first }
        else { getLast(diffLst.rest) }
    }
}
/**
 * Removes the last Diff from the list.
 * @param diffLst the list to remove from
 * @return the list without its last Diff, or the empty list if `diffLst` is empty
 */
fun removeLast (diffLst : DiffList): DiffList {
    return when (diffLst) {
        is Dempty -> Dempty
        is Dnode -> if (diffLst.rest is Dempty)  { Dempty }
                    else { Dnode(diffLst.first, removeLast(diffLst.rest)) }
    }
}





