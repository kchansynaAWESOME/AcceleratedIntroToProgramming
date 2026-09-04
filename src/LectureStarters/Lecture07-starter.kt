package LectureStarters

import io.kotest.matchers.shouldBe

/*---------------------------------------*/
/* Lecture-7 Objectives:
 * ----------------------------------------
 * 1. Mutually recursive data: a Person holds a PeopleList, a PeopleList holds Persons
 *    - this is how we represent an arbitrary-arity tree (a person can have any number of children)
 * 2. HtDF for mutually recursive data
 *    - youngerThan / youngerThanLOP
 *    - isDescendant / isDescendantLOP
 * 3. Carrying a "best so far" through a tree: youngest / youngestLOP
 * 4. Producing (rebuilding) mutually recursive data: birthday / birthdayLOP
 *    - data is immutable, so "changing" one person means returning a new copy of the tree
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
 *  Mutually Recursive Data Types (Person and PeopleList)
 * ========================================== */

//  TODO: We want to represent a person; a person can have children which themselves are people
/**
 * A person and the basic information about them
 * @property name the person's name
 * @property age the person's age
 * @property children the person's children
 */
data class Person(val name: String, val age: Int, val children: PeopleList)

/**
 * A list of people
 * @property first the first person in the list
 * @property rest the rest of the list
 * @return the list of people
 */
sealed interface PeopleList {
    data object Empty : PeopleList
    data class Node(val first : Person, val rest : PeopleList) : PeopleList
}
typealias pEmpty = PeopleList.Empty
typealias pNode = PeopleList.Node

/* ==========================================
 *  TESTS
 * ========================================== */

/**
 *  ![Arbitrary Arity Tree](./src/images//arbitraryaritytree.png )
 */
// people with no children
val p1 = Person("P1", 30, pEmpty)
val p2 = Person("P2", 31, pEmpty)
val p3 = Person("P3", 20, pEmpty)
val p4 = Person("P4", 15, pEmpty)
// parents
val p5 = Person("P5", 52, pNode(p1, pNode(p2, pNode(p3, pEmpty))))
val p6 = Person("P6", 40, pNode(p4, pEmpty))
val p7 = Person("P7", 55, pEmpty)
// grandparent
val p8 = Person("P8", 75, pNode(p5, pNode(p6, pNode(p7, pEmpty))))

// A second family tree.  Person is a data class, so "the same person" means structurally equal:
// q1 sits in three places in q4's tree, and a birthday has to reach every copy.
val q1 = Person("Q1", 10, pEmpty)
val q2 = Person("Q2", 35, pNode(q1, pEmpty))                        // q1 is q2's child ...
val q3 = Person("Q3", 38, pNode(q1, pEmpty))                        // ... and q3's child ...
val q4 = Person("Q4", 60, pNode(q2, pNode(q3, pNode(q1, pEmpty))))  // ... and q4's own child

/* ==========================================
 * | Template for Mutually Recursive Functions
 * ========================================== */
/*
fun fn(person: Person): ... {
    (
            person.name
            person.age
            person.children
            fnLOP(person.children)
    )
}

// helper for fn
fun fnLOP(peopleList: PeopleList): ... {
    return when (peopleList) {
        is pEmpty -> ...
        is pNode ->
                     fn(peopleList.first)
                     fnLOP(peopleList.rest)
    }
}
*/

fun main() {

    // youngerThan tests
    youngerThan(p8, 40) shouldBe pNode(p1, pNode(p2, pNode(p3, pNode(p4, pEmpty))))

    // youngerThanLOP tests
    youngerThanLOP(pNode(p5, pNode(p6, pNode(p7, pEmpty))), 41) shouldBe
            pNode(p1, pNode(p2, pNode(p3, pNode(p6, pNode(p4, pEmpty)))))

    // youngestSlow tests
    youngestSlow(p8) shouldBe p4

    // youngestSlowLOP tests
    youngestSlowLOP(pNode(p1, pNode(p2, pNode(p8, pEmpty)))) shouldBe p4

    // youngest tests
    youngest(p8) shouldBe p4

    // youngestLOP tests
    youngestLOP(pNode(p1, pNode(p2, pNode(p8, pEmpty)))) shouldBe p4

    // isDescendant tests
    isDescendant(p8, p4) shouldBe true          // grandchild, found through p6

    // isDescendantLOP tests
    isDescendantLOP(pNode(p1, pNode(p2, pEmpty)), p3) shouldBe false

    // birthday tests
      birthday(p8, p4) shouldBe
            Person("P8", 75, pNode(p5,
                    pNode(Person("P6", 40, pNode(Person("P4", 16, pEmpty), pEmpty)),
                            pNode(p7, pEmpty))))

    // birthdayLOP tests
    birthdayLOP(p8.children, p4) shouldBe
            pNode(p5, pNode(Person("P6", 40, pNode(Person("P4", 16, pEmpty), pEmpty)),
                    pNode(p7, pEmpty)))
}

/* ==========================================
 *  Mutually Recursive Functions
 * ========================================== */
// helper function
/**
 * Appends two lists of people together
 * @param plist1 the list whose people come first
 * @param plist2 the list whose people come second
 * @return one list: all of plist1's people, in order, followed by all of plist2's
 */
fun append (plist1 : PeopleList, plist2 : PeopleList): PeopleList {
    return when (plist1) {
        is pEmpty -> plist2
        is pNode -> pNode (plist1.first, append (plist1.rest, plist2))
    }
}

// youngerThan
/**
 * Produces a list of all of that person's descendants younger than the specified age (including self)
 * @param person the person whose descendants we are interested in
 * @param cutoff the age threshold
 * @return a list of all descendants younger than the specified age (including self)
 */
fun youngerThan(person: Person, cutoff: Int): PeopleList {
    // inventory: person.name; person.age; person.children; cutoff; youngerThanLOP(person.children, cutoff)
    return if (person.age < cutoff) {
        pNode(person,youngerThanLOP(person.children,cutoff))
    } else {
        youngerThanLOP(person.children,cutoff)
    }
}

// helper for youngerThan
/**
 * Produces a list of everyone in the list, and their descendants, younger than [cutoff]
 * @param peopleList the people whose descendants we are interested in
 * @param cutoff the age threshold
 * @return a list of all those people and descendants younger than the cutoff, in list order
 */
fun youngerThanLOP(peopleList: PeopleList, cutoff: Int): PeopleList {
    // inventory: peopleList.first; peopleList.rest; cutoff; youngerThan(peopleList.first, cutoff); youngerThanLOP(peopleList.rest, cutoff)
    return when (peopleList) {
        is pEmpty -> pEmpty
        is pNode -> append(youngerThan(peopleList.first,cutoff),youngerThanLOP(peopleList.rest, cutoff))
    }
}

/**
 * finds the youngest among the person's descendants (including themselves)
 * @param person the person whose descendants we are interested in
 * @return the youngest among the person's descendants (including themselves).
 * Favors person who can be found earlier in the arbitrary arity tree
 */
fun youngestSlow (person: Person): Person {
    // inventory: person.name; person.age; person.children; youngestSlowLOP(person.children)
    val small = youngestSlowLOP(person.children)
    return if (person.age < small.age) person else small
}

// helper for youngestSlow
/**
 * finds the youngest person in the list, counting their descendants too
 * @param peopleList the people to search
 * @return the youngest of them; a max default of age 1000 when the list is empty
 */
fun youngestSlowLOP(peopleList: PeopleList): Person {
    // inventory: peopleList.first; peopleList.rest; youngestSlow(peopleList.first); youngestSlowLOP(peopleList.rest)
    return when (peopleList) {
        is pEmpty -> Person("old person",1000, pEmpty)
        is pNode -> {
            val smallestFirst = youngestSlow(peopleList.first)
            val smallestRest = youngestSlowLOP(peopleList.rest)
            if (smallestFirst.age < smallestRest.age) smallestFirst else smallestRest
        }

    }
}
/**
 * finds the youngest among the person's descendants (including themselves)
 * @param person the person whose descendants we are interested in
 * @return the youngest among the person's descendants (including themselves)
 */
fun youngest (person: Person): Person {
    // inventory: person.name; person.age; person.children; youngestLOP(person.children)
    return person // TODO: Stub - Replace Me
}
// helper for youngest
/**
 * finds the youngest person in the list, counting their descendants too
 * @param peopleList the people to search
 * @return the youngest of them; a max default of age 1000 when the list is empty
 */
fun youngestLOP(peopleList: PeopleList): Person {
    // inventory: peopleList.first; peopleList.rest; youngest(peopleList.first); youngestLOP(peopleList.rest)
    return p1  // TODO: Stub - Replace Me
}

/**
 * Checks if one person is a descendant of another
 * @param p1 the person to check against (parent)
 * @param p2 the person to check (child)
 * @return true if p1 is a descendant of p2, false otherwise
 */
fun isDescendant (p1: Person, p2: Person ): Boolean {
    // inventory: p1.name; p1.age; p1.children; p2; p1 == p2; isDescendantLOP(p1.children, p2)
    return (p1 == p2) || isDescendantLOP(p1.children, p2)
}

// helper for isDescendant
/**
 * Checks whether a person appears anywhere under a list of people
 * @param people the people whose trees we search
 * @param p2 the person to look for
 * @return true if p2 is one of these people or a descendant of one, false otherwise
 */
fun isDescendantLOP (people: PeopleList, p2: Person ): Boolean {
    // inventory: people.first; people.rest; p2; isDescendant(people.first, p2); isDescendantLOP(people.rest, p2)
    return when (people) {
        is pEmpty -> false
        is pNode -> isDescendant(people.first,p2) || isDescendantLOP(people.rest,p2)
    }
}

/**
 * Updates the age of the person on their birthday
 * @param p1 the person at the root of the tree to rebuild
 * @param p2 the person having the birthday
 * @return a copy of p1's tree in which p2's age is one year greater
 */
fun birthday(p1: Person, p2: Person): Person {
    // inventory: p1.name; p1.age; p1.age + 1; p1.children; p2; p1 == p2; birthdayLOP(p1.children, p2)
    return p1  // TODO: Stub - Replace Me
}
// helper for birthday
/**
 * Gives one person a birthday everywhere they appear under a list of people
 * @param people the list of people to rebuild
 * @param p2 the person having the birthday
 * @return a copy of the list in which every occurrence of p2 is one year older
 */
fun birthdayLOP(people: PeopleList, p2: Person): PeopleList {
    // inventory: people.first; people.rest; p2; birthday(people.first, p2); birthdayLOP(people.rest, p2)
    return pEmpty  // TODO: Stub - Replace Me
}