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

//sealed interface StringList {
//    data object Empty : StringList
//    data class Node(val first : String, val rest : StringList) : StringList
//}
//typealias sEmpty = StringList.Empty
//typealias sNode = StringList.Node

data class Human(val suppliesTreats : Boolean, val naptimesInterrupted : Int, val furnitureSacrificed : Int)
data class Cat(val name : String, val livesRemaining : Int, val napsPerDay : Int, val cutenessLevel : Int, val staff : Human?)

sealed interface CatList {
    data object Empty : CatList
    data class Node(val first : Cat, val rest : CatList) : CatList
}
typealias cempty = CatList.Empty
typealias cnode = CatList.Node


fun getFirst (CatLst : CatList): Cat {
    return when (CatLst) {
        is cempty -> throw NoSuchElementException("List is empty!")
        is cnode ->  { CatLst.first }
    }
}

fun addFirst (Cat: Cat, CatLst : CatList): CatList {
    return cnode(Cat, CatLst)
}

fun removeFirst (CatLst : CatList): CatList {
    return when (CatLst) {
        is cempty -> cempty
        is cnode ->  { CatLst.rest }
    }
}

fun getLast (CatLst : CatList): Cat {
    return when (CatLst) {
        is cempty -> throw NoSuchElementException("List is empty!")
        is cnode -> if (CatLst.rest is cempty)  { CatLst.first }
        else { getLast(CatLst.rest) }
    }
}
fun removeLast (CatLst : CatList): CatList {
    return when (CatLst) {
        is cempty -> cempty
        is cnode -> if (CatLst.rest is cempty)  { cempty }
        else { cnode(CatLst.first, removeLast(CatLst.rest)) }
    }
}

/* ==========================================
 * Example values
 * ========================================== */

val goodStaff = Human(suppliesTreats = true, naptimesInterrupted = 0, furnitureSacrificed = 4)
val okStaff = Human(suppliesTreats = true, naptimesInterrupted = 3, furnitureSacrificed = 0)
val badStaff  = Human(suppliesTreats = false, naptimesInterrupted = 12, furnitureSacrificed = 0)

val whiskers = Cat("Whiskers", 9, 14, 3, goodStaff)
val mittens  = Cat("Mittens", 3, 20, 9, badStaff)
val snow = Cat("Snow", 9, 12, 10, okStaff)
val garfield = Cat("Garfield", 9, 16, 1, goodStaff)
val cookie = Cat("Cookie", 0, 0, 9, null)

val noCats = cempty
val oneCat = cnode(whiskers, cempty)
val someCats = cnode(whiskers, cnode(mittens, cnode(snow, cempty)))
val moreCats = cnode(snow, cnode(garfield, cnode(cookie, cnode(whiskers, cnode(mittens, cempty)))))
val spoiledCats = cnode(whiskers, cnode(garfield, cempty))

/** ==========================================
 * The template
 *
 *  when (cats) {
 *      is cempty -> ...
 *      is cnode -> cats.first ... cats.rest
 *  }
 *
 * The data is self-referential (a Node holds an CatList), so the function is self-referential too:
 *    * one branch per case, base case and recursive step.
 * There is no need for an else.
 * ========================================== */

fun main() {
    // Problem - 1
    catNames(someCats) shouldBe SNode("Whiskers", catNames(someCats.rest))
    catNames(someCats) shouldBe SNode("Whiskers", SNode("Mittens", SNode("Snow", SEmpty)))
    catNames(noCats) shouldBe SEmpty
    // Problem - 2

    lazyCats(someCats) shouldBe SNode("Whiskers", lazyCats(someCats.rest))
    lazyCats(someCats) shouldBe SNode("Whiskers", SNode("Mittens", lazyCats((someCats.rest as cnode).rest)))
    lazyCats(someCats) shouldBe SNode("Whiskers", SNode("Mittens", lazyCats(cempty))) // Snow is not lazy

    // Problem - 3
    catsWithGoodStaff(someCats) shouldBe cnode(whiskers, catsWithGoodStaff(someCats.rest)) // mittens does not have good staff
    catsWithGoodStaff(someCats) shouldBe cnode(whiskers, cnode(snow, catsWithGoodStaff(cempty))) // mittens does not have good staff

    // Problem - 4
    // lucky scores: whiskers 5, garfield 5, snow 4, mittens 2, cookie 1
    luckyCats(someCats) shouldBe cnode(whiskers, luckyCats(someCats.rest)) // mittens is not lucky
    luckyCats(someCats) shouldBe cnode(whiskers, luckyCats(cempty))        // mittens (2) and snow (4) are not lucky
    luckyCats(someCats) shouldBe cnode(whiskers, cempty)


    // Problem - 5
    // naps per day: whiskers 14, mittens 20, snow 12, garfield 16, cookie 0
    totalNapHours(someCats) shouldBe 14 + totalNapHours(someCats.rest)     // whiskers naps 14, trust the rest
    totalNapHours(someCats) shouldBe 14 + 20 + 12 + totalNapHours(cempty)  // whiskers, mittens, snow, then no cats left
    totalNapHours(someCats) shouldBe 46

    // Problem - 6
    // lives remaining: whiskers 9, mittens 3, snow 9, garfield 9, cookie 0
    nineLivesCats(someCats) shouldBe 1 + nineLivesCats(someCats.rest)      // whiskers has all 9, trust the rest
    nineLivesCats(someCats) shouldBe 2                                     // whiskers and snow; mittens has 3

    // Problem - 7
    // lucky scores: whiskers 5, garfield 5, snow 4, mittens 2, cookie 1
    //luckiestCat(someCats) shouldBe getLuckyCat(whiskers, luckiestCat(someCats.rest))  // trust the rest
    luckiestCat(someCats) shouldBe whiskers                                 // whiskers 5, mittens 2, snow 4

    // Problem - 8
    // naps per day: mittens 20, garfield 16, whiskers 14, snow 12, cookie 0
    laziestCat(someCats) shouldBe mittens                                   // 14, 20, 12

}

/* ==========================================
 * | STUBS
 * ========================================== */
// ---  MAPPING  -----
// Problem - 1
/**
 * Returns a list of the names of all the cats in the given list.
 * @param cats the list of cats
 * @return names of all the cats in the given list
 */
fun catNames (cats : CatList) : StringList {
    // Inventory: cempty;  cnode ; cats.first;  cats.rest;  cats.first.name;  catNames(...);  sNode(...);  sEmpty
    return when (cats) {
        is cempty -> SEmpty
        is cnode -> SNode(cats.first.name,catNames(cats.rest))
    }
}

// Problem - 2
/**
 * Returns a list of the names of all the lazy cats (i.e., those who sleep more than 12 hrs a day) in the given list.
 * @param cats the list of cats
 * @return names of all the lazy cats in the given list
 */
fun lazyCats (cats : CatList) : StringList {
   return when (cats) {
       is cempty -> SEmpty
       is cnode -> if (cats.first.napsPerDay > 12) SNode(cats.first.name,lazyCats(cats.rest)) else lazyCats(cats.rest)
   }
}

// ---  FILTERING  -----

// Problem - 3
/**
 * Returns a list of all the cats in the given list whose staff member gives treats.
 * @param cats the list of cats
 * @return all the cats in the given list whose staff member gives treats
 */
fun catsWithGoodStaff (cats : CatList) : CatList {
    // Inventory: ???
    return cempty // TODO: Stub - Replace Me
}

// Problem - 4
/**
 * Returns a list of all the lucky cats. A cat is "lucky" if their lucky score is at least 5.
 * @param cats the list of cats
 * @return all the cats with a lucky score of at least 5
 */
fun luckyCats (cats : CatList) : CatList {
    // Inventory: ???
    return cempty // TODO: Stub - Replace Me
}

// ---  FOLDING ------

// Problem - 5
/**
 * Returns the total number of hours that all the cats in the given list have napped.
 * @param cats the list of cats
 * @return the total number of nap hours
 */
fun totalNapHours (cats : CatList) : Int {
    // Inventory: ???
    return 0 // TODO: Stub - Replace Me
}

// Problem - 6
/** Returns the number of cats having all their 9 lives.
 * @param cats the list of cats
 * @return the number of cats with all their 9 lives
 */
fun nineLivesCats (cats : CatList) : Int {
    // Inventory: ???
    return 0  // TODO: Stub - Replace Me
}

// Problem - 7
/** Returns the cat having the largest lucky score.
 * If several cats tie for the largest score, returns the one nearest the front of the list.
 * @param cats the list of cats
 * @return the cat with the largest lucky score
 * @throws NoSuchElementException if the list is empty, as there is no cat to return
 */
fun luckiestCat (cats : CatList) : Cat {
    // Inventory: ???
    return mittens // TODO: Stub - Replace Me
}

// Problem - 8
/** Returns the cat that naps most number of hours.
 * If several cats tie for the most naps, returns the one nearest the front of the list.
 * @param cats the list of cats
 * @return the cat that naps most number of hours
 * @throws NoSuchElementException if the list is empty, as there is no cat to return
 */
fun laziestCat (cats : CatList) : Cat {
    // Inventory: ???
    return mittens // TODO: Stub - Replace Me
}


