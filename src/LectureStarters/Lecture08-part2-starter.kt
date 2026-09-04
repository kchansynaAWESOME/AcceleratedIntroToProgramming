package LectureStarters

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

/*---------------------------------------*/
/* Lecture-8 (part 2) Objectives:
 * ----------------------------------------
 * 1. Abstraction: what stays the same (the traversal) vs. what varies (the lambda)
 * 2. Recognizing the three patterns in the recursion we already wrote, and reimplementing
 *    the Lecture-8 problems on top of them (catNamesMap, luckyCatsFilter, totalNapHoursFold, ...)
 * 3. Higher order functions over a list: mapCatList, filterCatList, foldCatList
 * 4. Function types and lambdas : https://kotlinlang.org/docs/lambdas.html
 *    - (Cat) -> String as a type; storing a function in a val; "it" for a single parameter
 * 5. Passing functions to functions : function references (::getName), lambda variables,
 *    inline lambdas, and trailing-lambda syntax
 * 6. Generic types : https://kotlinlang.org/docs/generics.html
 *    - AnyList<out T> holds any element type; covariance lets one Empty object serve them all
 */

/* ***************************************
 * How to Design Functions (HtDF)
 *    1. Stub (Signature + body/return) + Documentation
 *    2. Example test
 *    3. inventory & template (Work forwards with what we can do with the input we have?)
 *    4. Implement - work backwards from output cases, local variables/helper functions
 *    5. Sufficient Testing and Debugging
 *************************************** */

/* ==========================================
 * | Generic List - AnyList<out T>
 * ========================================== */

// <out T> makes it covariant, which is what lets a single Empty object serve every element type:
//    * Empty is a AnyList<Nothing>, and Nothing is a subtype of everything, so it's usable as AnyList<String>, AnyList<Int>, etc.
//    Without out, Empty would have to be a generic class instead of an object.

//sealed interface AnyList<out T> {
//    data object Empty : AnyList<Nothing>
//    data class Node<out T>(val first : T, val rest : AnyList<T>) : AnyList<T>
//}
//typealias AEmpty = AnyList.Empty
//typealias ANode<T> = AnyList.Node<T>

/* ==========================================
 * | Lambdas
 * ========================================== */
fun getName(c : Cat) : String {
    return c.name
}
// Define the getName() function as a lambda expression, which is a function without a name.
// The type of the lambda is (Cat) -> String, meaning it takes a Cat and returns a String.
val getNameLambda = { c: Cat -> c.name }  // type: (Cat) -> String

/**
 *   - The parameter type can be omitted — the compiler knows from (Cat) -> String that the parameter is a Cat, so c: Cat would be redundant.
 *   - With exactly one parameter and no name given, Kotlin supplies the name it automatically. So it is the cat.
 *   (it only exists for single-parameter lambdas; with two you must name them, e.g. { a, b -> ... }.)
 *   And a lambda's last expression is its return value , it.name , with no return keyword.
 */
val getNameLambda2: (Cat) -> String = { it.name }      // alternative way to define the lambda using it


fun main() {
    /* ==========================================
     * | HOW TO PASS "lambdas" to higher order functions. Several different ways:
     * 1. Pass a named function (like getName) as a parameter to the higher order function.
     *     Ex: mapCatList(someCats, ::getName)  // pass function reference
     *
     * 2. Pass a lambda expression (like getNameLambda) as a parameter to the higher order function.
     *     Ex: mapCatList(someCats, getNameLambda)  // pass lambda variable
     *
     * 3. Pass a lambda expression directly as a parameter to the higher order function, without naming it first.
     *     Ex: mapCatList(someCats, { c: Cat -> c.name })  // pass lambda directly
     *
     * 4. Pass a lambda expression directly as a parameter to the higher order function, using "it" to refer to the single parameter.
     *     Ex: mapCatList(someCats) { it.name }  // pass lambda directly, using "it"
     *     Kotlin has trailing-lambda syntax — if the last parameter of a function has a function type, a lambda literal written at the call site may be moved outside the parentheses.
     *     And if the lambda is then the only argument left, the parens disappear entirely. Both of the following  are the same call:
     *         mapCatList(someCats, { it.name })   // ordinary argument position
     *         mapCatList(someCats) { it.name }    // moved out ; the idiomatic form
     *      The reason the language does this is readability: it makes mapCatList(...) { ... } read like a built-in block,
     * ========================================== */


    mapCatList(noCats, getNameLambda)  shouldBe AEmpty
    mapCatList(oneCat, ::getName)  shouldBe ANode("Whiskers", AEmpty)
    mapCatList(oneCat, getNameLambda)  shouldBe ANode("Whiskers", AEmpty)

    /**     The following three are all the same, just different ways to pass the lambda function */
    mapCatList(someCats, getNameLambda) shouldBe ANode("Whiskers", mapCatList(someCats.rest, getNameLambda))
    // same as above, directly passed the lambda function
    mapCatList(someCats, { c: Cat -> c.name }) shouldBe ANode("Whiskers", mapCatList(someCats.rest, getNameLambda))
    // same as above, directly passed the alternative definition of lambda function using "it".
    mapCatList(someCats) { it.name } shouldBe ANode("Whiskers", mapCatList(someCats.rest, getNameLambda))

    // any other type works too: Cat -> Int gives an AnyList<Int> ...
    mapCatList(someCats) { it.napsPerDay } shouldBe ANode(14, ANode(20, ANode(12, AEmpty)))
    // ... Cat -> Int gives an AnyList<Int> ...
    mapCatList(spoiledCats) { it.livesRemaining } shouldBe ANode(9, ANode(9, AEmpty))
    // ... Cat -> Boolean gives an AnyList<Boolean> ...
    mapCatList(moreCats) { it.livesRemaining == 9 } shouldBe ANode(true, ANode(true, ANode(false, ANode(true, ANode(false, AEmpty)))))
    // ... and Cat -> Cat just copies the cats into an AnyList
    mapCatList(oneCat) { it } shouldBe ANode(whiskers, AEmpty)

    // Problem - 1
    catNamesMap(oneCat) shouldBe ANode("Whiskers", AEmpty)
    catNamesMap(noCats) shouldBe AEmpty
    catNamesMap(someCats) shouldBe ANode("Whiskers", catNamesMap(someCats.rest))
    catNamesMap(someCats) shouldBe ANode("Whiskers", ANode("Mittens", ANode("Snow", AEmpty)))
    catNamesMap(someCats) shouldBe ANode("Whiskers", ANode("Mittens", ANode("Snow", AEmpty)))
    catNamesMap(moreCats) shouldBe ANode("Snow", ANode("Garfield", ANode("Cookie", ANode("Whiskers", ANode("Mittens", AEmpty)))))
    catNamesMap(spoiledCats) shouldBe ANode("Whiskers", ANode("Garfield", AEmpty))

    // Problem - 3 - Higher Order Solution
    catsWithGoodStaff(oneCat) shouldBe cnode(whiskers, cempty)
    catsWithGoodStaff(someCats) shouldBe cnode(whiskers, catsWithGoodStaff(someCats.rest)) // mittens does not have good staff
    catsWithGoodStaff(someCats) shouldBe cnode(whiskers, cnode(snow, catsWithGoodStaff(cempty))) // mittens does not have good staff
    catsWithGoodStaff(moreCats) shouldBe cnode(snow, cnode(garfield, cnode(whiskers, cempty))) // cookie and  mittens don't have good staff
    catsWithGoodStaff(spoiledCats) shouldBe spoiledCats  // all spoiled cats have good staff

    // Problem - 2 - Hisgher Order Solution
    lazyCatsFilterMap(oneCat) shouldBe ANode("Whiskers", AEmpty)
    lazyCatsFilterMap(someCats) shouldBe ANode("Whiskers", ANode("Mittens", AEmpty)) // snow is not lazy
    lazyCatsFilterMap(someCats) shouldBe ANode("Whiskers", lazyCatsFilterMap(someCats.rest))
    lazyCatsFilterMap(someCats) shouldBe ANode("Whiskers", ANode("Mittens", lazyCatsFilterMap((someCats.rest as cnode).rest)))
    lazyCatsFilterMap(someCats) shouldBe ANode("Whiskers", ANode("Mittens", lazyCatsFilterMap(cempty))) // Snow is not lazy
    lazyCatsFilterMap(moreCats) shouldBe ANode("Garfield", ANode("Whiskers", ANode("Mittens", AEmpty))) // Snow and Cookie are not lazy
    lazyCatsFilterMap(spoiledCats) shouldBe ANode("Whiskers", ANode("Garfield", AEmpty)) // all spoiled cats are lazy

    // Problem - 4 - Higher Order Solution
    // lucky scores: whiskers 5, garfield 5, snow 4, mittens 2, cookie 1
    luckyCatsFilter(noCats) shouldBe cempty
    luckyCatsFilter(oneCat) shouldBe cnode(whiskers, cempty)                     // whiskers scores 5
    luckyCatsFilter(someCats) shouldBe cnode(whiskers, luckyCatsFilter(someCats.rest)) // mittens is not lucky
    luckyCatsFilter(someCats) shouldBe cnode(whiskers, luckyCatsFilter(cempty))        // mittens (2) and snow (4) are not lucky
    luckyCatsFilter(someCats) shouldBe cnode(whiskers, cempty)
    luckyCatsFilter(moreCats) shouldBe cnode(garfield, cnode(whiskers, cempty))  // snow, cookie and mittens are not lucky
    luckyCats(spoiledCats) shouldBe spoiledCats                            // both spoiled cats are lucky

    // Problem - 5
    // naps per day: whiskers 14, mittens 20, snow 12, garfield 16, cookie 0
    totalNapHoursFold(noCats) shouldBe 0
    totalNapHoursFold(oneCat) shouldBe 14
    totalNapHoursFold(someCats) shouldBe 14 + totalNapHoursFold(someCats.rest)     // whiskers naps 14, trust the rest
    totalNapHoursFold(someCats) shouldBe 14 + 20 + 12 + totalNapHoursFold(cempty)  // whiskers, mittens, snow, then no cats left
    totalNapHoursFold(someCats) shouldBe 46
    totalNapHoursFold(moreCats) shouldBe 62                                    // 12 + 16 + 0 + 14 + 20
    totalNapHoursFold(spoiledCats) shouldBe 30                                 // 14 + 16

    // Problem - 6
    // lives remaining: whiskers 9, mittens 3, snow 9, garfield 9, cookie 0
    nineLivesCatsFold(noCats) shouldBe 0
    nineLivesCatsFold(oneCat) shouldBe 1
    nineLivesCatsFold(someCats) shouldBe 1 + nineLivesCats(someCats.rest)      // whiskers has all 9, trust the rest
    nineLivesCatsFold(someCats) shouldBe 2                                     // whiskers and snow; mittens has 3
    nineLivesCatsFold(moreCats) shouldBe 3                                     // snow, garfield, whiskers; cookie has 0
    nineLivesCatsFold(spoiledCats) shouldBe 2                                  // both spoiled cats have all 9

    // Problem - 7
    // lucky scores: whiskers 5, garfield 5, snow 4, mittens 2, cookie 1
    shouldThrow<NoSuchElementException> { luckiestCat(noCats) }                          // no cat to return
    luckiestCatFold(oneCat) shouldBe whiskers
    //luckiestCatFold(someCats) shouldBe getLuckyCat(whiskers, luckiestCat(someCats.rest))  // trust the rest
    luckiestCatFold(someCats) shouldBe whiskers                                 // whiskers 5, mittens 2, snow 4
//    luckiestCatFold(moreCats) shouldBe garfield                                 // garfield and whiskers tie at 5, earlier one wins
//    luckiestCatFold(spoiledCats) shouldBe whiskers                              // tie at 5 again, earlier one wins

    // Problem - 8
    // naps per day: mittens 20, garfield 16, whiskers 14, snow 12, cookie 0
    shouldThrow<NoSuchElementException> { laziestCat(noCats) }                           // no cat to return
    laziestCatFold(oneCat) shouldBe whiskers
    laziestCatFold(someCats) shouldBe mittens                                   // 14, 20, 12
    laziestCatFold(moreCats) shouldBe mittens                                   // 12, 16, 0, 14, 20
    laziestCatFold(spoiledCats) shouldBe garfield                               // 16 beats 14

}

/* ==========================================
 * | STUBS
 * ========================================== */

// ---  MAPPING  -----

/* ==========================================
 * | Generalize "mapping" solution as a higher order function
 * ========================================== */

/**
 * Applies the given function to every cat in the given list, producing a new list of the results.
 * The output list has the same length and order as the input list.
 * @param T the type of value the function produces for each cat
 * @param cats the list of cats
 * @param fn the function to apply to each cat
 * @return a list holding fn applied to each cat, in the same order
 */
fun <T> mapCatList(cats: CatList, fn: (Cat)->T): AnyList<T> {
    return when (cats) {
        is cempty -> AEmpty
        is cnode  -> ANode(fn(cats.first),mapCatList(cats.rest,fn))
    }
}

// Reimplement Problem-1 using mapCatList
/**
 * Returns a list of the names of all the cats in the given list. Calls mapCatList instead of using recursion.
 * @param cats the list of cats
 * @return names of all the cats in the given list
 */
fun catNamesMap (cats : CatList) : AnyList<String> {
    return mapCatList(cats) {c -> c.name}
}

/* ==========================================
 * | Generalize "filtering" solution as a higher order function
 * ========================================== */

/**
 * Keeps only the cats in the given list that satisfy the given test, dropping the rest.
 * @param cats the list of cats
 * @param fn the test applied to each cat; true keeps the cat, false drops it
 * @return a list of the cats for which fn returns true, in the same order
 */
fun filterCatList(cats: CatList, fn: (Cat)->Boolean): CatList {
    return when (cats) {
        is cempty -> cempty
        is cnode -> if (fn(cats.first)) cnode(cats.first,filterCatList(cats.rest,fn))
            else filterCatList(cats.rest,fn)

    }
}

// Reimplement Problem - 3 using filterCatList
/**
 * Returns a list of all the cats in the given list whose staff member gives treats. Calls filterCatList instead of using recursion
 * @param cats the list of cats
 * @return all the cats in the given list whose staff member gives treats
 */
fun catsWithGoodStaffFilter (cats : CatList) : CatList {
    return filterCatList(cats) {c -> c.staff?.suppliesTreats == true}
}

// Reimplement Problem - 2 using filterCatList and mapCatList
/**
 * Returns a list of the names of all the lazy cats (i.e., those who sleep more than 12 hrs a day) in the given list.
 *  Calls filterCatList and mapCatList instead of using recursion.
 * @param cats the list of cats
 * @return names of all the lazy cats in the given list
 */
fun lazyCatsFilterMap (cats : CatList) : AnyList<String> {
    return AEmpty
}

// Reimplement Problem - 4 using filterCatList
///**
/**
 * Returns a list of all the lucky cats. A cat is "lucky" if their lucky score is at least 5.
 * Calls filterCatList instead of using recursion.
 * @param cats the list of cats
 * @return all the cats with a lucky score of at least 5
 */
fun luckyCatsFilter (cats : CatList) : CatList {
    return cempty
}

/* ==========================================
 * | Generalize "folding" solution as a higher order function
 * ========================================== */
/**
 * Combines all the cats in the given list into a single value.
 * @param T the type of the accumulated value
 * @param cats the list of cats
 * @param init the value to start with when there are no cats left
 * @param fn combines the accumulated value so far with one cat
 * @return the accumulated value after every cat has been combined
 */
fun <T> foldCatList (cats: CatList, init: T, fn: (T, Cat) -> T): T {
    return init
}

// Reimplement Problem - 5 using foldCatList
/**
 * Returns the total number of hours that all the cats in the given list have napped.
 * Calls foldCatList and mapCatList instead of using recursion.
 * @param cats the list of cats
 * @return the total number of nap hours
 */
fun totalNapHoursFold (cats : CatList) : Int {
    return 0
}

// Reimplement Problem - 6 using filterCatList and foldCatList
/**
 * Returns the number of cats having all their 9 lives.
 * Calls filterCatList and foldCatList instead of using recursion.
 * @param cats the list of cats
 * @return the number of cats with all their 9 lives
 */
fun nineLivesCatsFold (cats : CatList) : Int {
    return 0
}

// Reimplement Problem - 7 using foldCatList
/**
 * Returns the cat having the largest lucky score.
 * Currently, this does not work with cases where there is a tie for the lucky scores. We will revise this solution after we talk about tail-recursion.
 * Calls foldCatList and mapCatList instead of using recursion.
 * @param cats the list of cats
 * @return the cat with the largest lucky score. Favors the leftmost cat if there is a tie for the max score.
 * @throws NoSuchElementException if the list is empty, as there is no cat to return
 */
fun luckiestCatFold (cats : CatList) : Cat {
    return mittens
}

// Reimplement Problem - 8 using foldCatList
/** Returns the cat that naps most number of hours.
 * If several cats tie for the most naps, returns the one nearest the front of the list.
 * Calls foldCatList and mapCatList instead of using recursion.
 * @param cats the list of cats
 * @return the cat that naps most number of hours
 * @throws NoSuchElementException if the list is empty, as there is no cat to return
 */
fun laziestCatFold (cats : CatList) : Cat {
    return mittens
}

