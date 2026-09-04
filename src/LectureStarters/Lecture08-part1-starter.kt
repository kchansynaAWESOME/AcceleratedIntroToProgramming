package LectureStarters

import LectureStarters.AnyList.Node
import io.kotest.matchers.shouldBe


/*---------------------------------------*/
/* Lecture-8 (part 2) Objectives:
 * ----------------------------------------
 * 1. Abstraction: what stays the same (the traversal) vs. what varies (the lambda)
 * 2. Higher order functions
 */

//sealed interface IntList {
//    data object Empty : IntList
//    data class Node(val first: Int, val rest: IntList) : IntList
//}
//typealias IEmpty = IntList.Empty
//typealias INode = IntList.Node
//
//sealed interface StringList {
//    data object Empty : StringList
//    data class Node(val first : String, val rest : StringList) : StringList
//}

sealed interface AnyList<out T> {
    data object Empty : AnyList<Nothing>
    data class Node<out T>(val first: T,val rest : AnyList<T>) : AnyList<T>
}

typealias AEmpty = AnyList.Empty
typealias ANode<T> = AnyList.Node<T>

fun main(){

    fun compare(a:Int, b:Int) : Boolean {
        return a < b
    }

    // insertionSort tests
    insertionSort(IEmpty) shouldBe IEmpty
    insertionSort(INode(2, INode(1, INode(3, IEmpty)))) shouldBe INode(1, INode(2, INode(3, IEmpty)))
    insertionSort(INode(3, INode(2, INode(1, INode(0, IEmpty))))) shouldBe
            INode(0, INode(1, INode(2, INode(3, IEmpty))))

    // insertionSortDesc tests
    insertionSortDesc(IEmpty) shouldBe IEmpty
    insertionSortDesc(INode(2, INode(1, INode(3, IEmpty)))) shouldBe INode(3, INode(2, INode(1, IEmpty)))
    insertionSortDesc(INode(0, INode(1, INode(5, INode(3, IEmpty))))) shouldBe
            INode(5, INode(3, INode(1, INode(0, IEmpty))))

    // insertionSortCats tests -- cats come back in alphabetical order by name
    insertionSortCats(noCats) shouldBe cempty
    insertionSortCats(oneCat) shouldBe cnode(whiskers, cempty)
    insertionSortCats(someCats) shouldBe cnode(mittens, cnode(snow, cnode(whiskers, cempty)))
    insertionSortCats(spoiledCats) shouldBe cnode(garfield, cnode(whiskers, cempty))
    // already in order, so the list comes back unchanged
    insertionSortCats(cnode(garfield, cnode(whiskers, cempty))) shouldBe cnode(garfield, cnode(whiskers, cempty))
    insertionSortCats(moreCats) shouldBe
            cnode(cookie, cnode(garfield, cnode(mittens, cnode(snow, cnode(whiskers, cempty)))))
    // sorting again changes nothing
    insertionSortCats(insertionSortCats(moreCats)) shouldBe insertionSortCats(moreCats)

    // insertCat tests
    insertCat(whiskers, cempty) shouldBe cnode(whiskers, cempty)                             // insert to empty list
    insertCat(cookie, cnode(garfield, cnode(whiskers, cempty))) shouldBe        // inserts to front
            cnode(cookie, cnode(garfield, cnode(whiskers, cempty)))
    insertCat(snow, cnode(garfield, cnode(whiskers, cempty))) shouldBe          // inserts to middle
            cnode(garfield, cnode(snow, cnode(whiskers, cempty)))
    insertCat(whiskers, cnode(cookie, cnode(garfield, cempty))) shouldBe        // inserts to last
            cnode(cookie, cnode(garfield, cnode(whiskers, cempty)))

    /* ==========================================
     * | Repeat same tests using "generalSort" -- one function, and the lambda decides the order
     * ========================================== */

    // the same list of cats as moreCats, but in an AnyList so generalSort can take it
    val someInts = ANode(2, ANode(1, ANode(3, AEmpty)))
    val allCats = ANode(snow, ANode(garfield, ANode(cookie, ANode(whiskers, ANode(mittens, AEmpty)))))

    // ascending numbers: the lambda replaces insertionSort
    generalSort(someInts,::compare) shouldBe ANode(1, ANode(2, ANode(3, AEmpty)))
    generalSort(ANode(3, ANode(2, ANode(1, ANode(0, AEmpty))))) { a, b -> a < b } shouldBe
            ANode(0, ANode(1, ANode(2, ANode(3, AEmpty))))

    // descending numbers: same data, same function, only the lambda flipped
    generalSort(someInts) { a, b -> a > b } shouldBe ANode(3, ANode(2, ANode(1, AEmpty)))
    generalSort(ANode(0, ANode(1, ANode(5, ANode(3, AEmpty))))) { a, b -> a > b } shouldBe
            ANode(5, ANode(3, ANode(1, ANode(0, AEmpty))))

    // cats: the lambda also chooses which field to sort on
    generalSort(allCats) { a, b -> a.name < b.name } shouldBe          // alphabetical, like insertionSortCats
            ANode(cookie, ANode(garfield, ANode(mittens, ANode(snow, ANode(whiskers, AEmpty)))))
    generalSort(allCats) { a, b -> a.napsPerDay > b.napsPerDay } shouldBe   // laziest cat first
            ANode(mittens, ANode(garfield, ANode(whiskers, ANode(snow, ANode(cookie, AEmpty)))))

    // generalInsert: one item into an already sorted list
    generalInsert(4, ANode(1, ANode(3, ANode(5, AEmpty)))) { a, b -> a < b } shouldBe   // into the middle
            ANode(1, ANode(3, ANode(4, ANode(5, AEmpty))))
    generalInsert(snow, ANode(garfield, ANode(whiskers, AEmpty))) { a, b -> a.name < b.name } shouldBe
            ANode(garfield, ANode(snow, ANode(whiskers, AEmpty)))


}
/* ==========================================
 * | Insertion Sort
 * ========================================== */

/**
 * Helper for insertionSort
 * Inserts [num] into the sorted list [lon], keeping it in increasing order.
 */
fun insert (num: Int, lon: IntList): IntList {
    return when (lon) {
        is IEmpty -> INode(num, IEmpty)
        is INode -> {
            if (num < lon.first) {
                INode(num, lon)
            } else {
                INode(lon.first, insert(num, lon.rest))
            }
        }
    }
}
/**
 * Sorts [lon] into increasing order using insertion sort.
 *
 * @param lon the list of numbers to sort
 * @return a new list with the same numbers in increasing order
 */
fun insertionSort(lon : IntList) : IntList {
    return when (lon) {
        is IEmpty -> IEmpty
        is INode -> insert(lon.first, insertionSort(lon.rest))
    }
}

/* ==========================================
 *  What if we want a function that sorts items in *descending* order?
 *  easy!  copy/paste
 * ========================================== */


/**
 * Helper for insertionSortDesc
 * Inserts [num] into the descending-sorted list [lon], keeping it in decreasing order.
 */
fun insertDesc (num: Int, lon: IntList): IntList {
    return when (lon) {
        is IEmpty -> INode(num, IEmpty)
        is INode -> {
            if (num > lon.first) {     // ONLY CHANGE
                INode(num, lon)
            } else {
                INode(lon.first, insertDesc(num, lon.rest))
            }
        }
    }
}
/**
 * Sorts [lon] into decreasing order using insertion sort.
 * @param lon the list of numbers to sort
 * @return a new list with the same numbers in increasing order
 */
fun insertionSortDesc(lon : IntList) : IntList {
    return when (lon) {
        is IEmpty -> IEmpty
        is INode -> insertDesc(lon.first, insertionSortDesc(lon.rest))
    }
}

/* ==========================================
 *  What if we want a function that sorts cats according to their name (in increasing alphabetical order))?
 * ========================================== */
/**
 * Helper for insertionSortCats
 * Inserts [cat] into the sorted list [loc], keeping it in increasing order by name.
 */
fun insertCat (cat: Cat, loc: CatList): CatList {
    return when (loc) {
        is cempty -> cnode(cat, cempty)
        is cnode -> {
            if (cat.name < (loc.first.name)) {
                cnode(cat, loc)
            } else {
                cnode(loc.first, insertCat(cat, loc.rest))
            }
        }
    }
}
/**
 * Sorts [loc] into increasing order according to Cat.name using insertion sort.
 * @param loc the cats to sort
 * @return a new list with the same cats in increasing order, alphabetically according to their name
 */
fun insertionSortCats(loc : CatList) : CatList {
    return when (loc) {
        is cempty -> cempty
        is cnode -> insertCat(loc.first, insertionSortCats(loc.rest))
    }
}

/* ==========================================
 * |  Let's write a more general sorting method that wil support all examples.
         IntList -> IntList
         StringList -> StringList
         CatList -> CatList
 Feels like we want a sort that can consume a list of an arbitrary
 type and produce the list sorted in *some* order

 Problem:  How do we know how to order the elements?
 Solution: Take in a function that orders them.
 * ========================================== */

/* ==========================================
 * | Generic List - AnyList<out T>
 * ========================================== */

// <out T> makes it covariant, which is what lets a single Empty object serve every element type:
//    * Empty is a StringList<Nothing>, and Nothing is a subtype of everything, so it's usable as StringList<String>, StringList<Int>, etc.
//    Without out, Empty would have to be a generic class instead of an object.

 // TODO -- define a generic list type.

// -------------------------------------------------------

// TODO -- define generalSort and generalInsert
/**
 * Helper for generalSort
 * Inserts [item] into [list], which is already sorted according to [compare], so that the result is still sorted.
 * @param T the type of the elements in the list
 * @param item the element to insert
 * @param list a list already sorted according to [compare]
 * @param compare returns true when its first argument comes before its second
 * @return a new list containing [item] and all elements of [list], still sorted by [compare]
 */


/**
 * Sorts [list] using insertion sort, ordering elements by [compare].
 * @param T the type of the elements in the list
 * @param list the list to sort
 * @param compare returns true when its first argument comes before its second
 * @return a new list with the same elements as [list], sorted by [compare]
 */

fun <T> generalInsert (item: T, loc: AnyList<T>,compare : (T,T) -> Boolean): AnyList<T> {
    return when (loc) {
        is AEmpty -> ANode(item, AEmpty)
        is ANode -> {
            if (compare(item, loc.first)) {
                ANode(item, loc)
            } else {
                ANode(loc.first, generalInsert(item, loc.rest,compare))
            }
        }
    }
}

fun <T> generalSort(list : AnyList<T>,compare : (T,T) -> Boolean) : AnyList<T> {
    return when (list) {
        is AEmpty -> AEmpty
        is ANode -> generalInsert(list.first, generalSort(list.rest,compare),compare)
    }
}

/* ==========================================
 * | Now we can use generalSort to sort any list, as long as we provide a comparison function. See the tests in "main" for examples.
 * ========================================== */


