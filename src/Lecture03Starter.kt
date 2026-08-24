import io.kotest.matchers.shouldBe
import java.time.Year

/*---------------------------------------*/
/* Lecture-3 Objectives:
 * ----------------------------------------
 * 1. Product types as Kotlin data classes : https://kotlinlang.org/docs/data-classes.html
 * 2. Nested/composed data classes : https://kotlinlang.org/docs/data-classes.html#data-classes
 * 3. HtDF for processing data classes : https://kotlinlang.org/docs/functions.html#function-parameters
 * 4. Structural equality (==) : https://kotlinlang.org/docs/equality.html
 */

/* ***************************************
 * How to Design Functions (HtDF)
 *    1. Stub (Signature + body/return) + Documentation
 *    2. Example test
 *    3. inventory & template (Work forwards with what we can do with the input we have?)
 *    4. Implement - work backwards from output cases, local variables/helper functions
 *    5. Sufficient Testing and Debugging
 *************************************** */

// Example - 1
// Let's look at an example of a data class.  We want to represent a school; we want to store,
//  *  what its called
//  *  where it is
//  *  when it was founded


/** A school and the basic information about it
 * @property name what the school is called, like "Worcester Polytechnic Institute"
 * @property location where the school is, like "Worcester, MA"
 * @property founded the year the school was founded, like 1865
 */
data class School (val name: String, val location: String, val founded: Int)

val wpi = School("WPI", "Worcester", 1865)
val cmu = School("CMU", "Pittsburgh", 1900)
val rpi = School("RPI", "Rochester", 1824)
val mit = School("MIT", "Boston", 1861)

//*************************************************
// We want to represent a student
//  * students have a name and a school
//  * schools have a name, location, year founded

// 1st attempt ; bad approach.
// data class Student(val name: String, val schoolName: String, val schoolLocation: String)

// 2nd attempt ; better approach
data class Student(val name: String, val school: School)

val joe = Student("Joe", wpi)
val sam = Student("Sam", School("Clark", "Worcester", 1863))
val ali = Student("Ali", School("WPI","Worcester", 1865))
val anand = Student("Anand", cmu)

//*************************************************
// There are only three ways two students can be related, so the answer is not
// really a String -- it is one of a fixed set of values.  That is an enum.
// https://kotlinlang.org/docs/enum-classes.html

/** How two students are related by where they go to school */
enum class Relationship { SCHOOLMATES, NEIGHBORS, STRANGERS }

//*************************************************
// Four things we can do with a data class:
// - define a new data type with a data class (like above)
// - construct a data object (value) with a constructor
// - deconstruct one with selectors ( dot-notation on fields)
// - predicate one with built-in functions: is and ==

// Recall HtDF Steps: 1. stub+docs, 2. example test, 3. inventory & template, 4. implementation, 5. testing/debugging


fun main() {
    howOld(wpi) shouldBe (2026 - 1865)

    schoolmates(ali, joe) shouldBe true

    neighbors(ali, sam) shouldBe true

    relationship(ali, joe) shouldBe Relationship.SCHOOLMATES

}

//// Problem - 1
///** howOld : Computes how many years a school has been open.
// * @param school the school to compute the age of
// * @return the number of years the school has been open
// */
fun howOld (school: School): Int {
    // Inventory : school.founded   Year.now().value
    return Year.now().value - school.founded
}

//// Problem - 2
///* schoolmates : Checks whether the two students attend the same school.
// * @param student1 the first student
// * @param student2 the second student
// * @return true if the two students attend the same school, false otherwise
// */
fun schoolmates( student1 : Student, student2 : Student) : Boolean {
    // Inventory : student.school  ==
    return student1.school == student2.school
}

// Problem - 3
// helper for relationship
/* neighbors : Checks whether the two students live in the same city.
 * @param student1 the first student
 * @param student2 the second student
 * @return true if the two students live in the same city, false otherwise
 */
fun neighbors( student1 : Student, student2 : Student) : Boolean {
    // Inventory : student1.school.location  ==
    return student1.school.location == student2.school.location
}

// Problem - 4
/** relationship : Describes how two students are related by where they go to school
 * @param student1 the first student
 * @param student2 the second student
 * @return SCHOOLMATES if they attend the same school,
 *         NEIGHBORS if they attend different schools in the same city,
 *         STRANGERS otherwise
 */
fun relationship(student1 : Student, student2 : Student) : Relationship {
    // Inventory: student1.school, student2.school, student1.school.location, student2.school.location,
    //            schoolmates(), neighbors(), ==, when
    return when {
        schoolmates(student1,student2) -> Relationship.SCHOOLMATES
        neighbors(student1,student2) -> Relationship.NEIGHBORS
        else -> Relationship.STRANGERS
    }
}

// Problem - 5:
/** greeting : Produces what one student says when they run into the other
 * @param aRelationship how the two students are related
 * @return the appropriate greeting for that relationship
 */
fun greeting(aRelationship : Relationship) : String {
    // Inventory: when, Relationship.SCHOOLMATES, Relationship.NEIGHBORS, Relationship.STRANGERS
    return when (aRelationship) {
        Relationship.SCHOOLMATES -> "hi"
        Relationship.NEIGHBORS -> "h"
        Relationship.STRANGERS -> " "
    }
}

// ANSWERS
