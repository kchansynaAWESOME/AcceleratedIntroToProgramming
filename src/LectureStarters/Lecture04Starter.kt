package LectureStarters

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

/*---------------------------------------*/
/* Lecture-4 Objectives:
 * ----------------------------------------
 * 1. Learn about sum types ("one of" data) with sealed interfaces
 *    // https://kotlinlang.org/docs/sealed-classes.html
 * 2. Consuming a sum type with an exhaustive when
 * 3. Producing a sum type */

/* ***************************************
 * How to Design Functions (HtDF)
 *    1. Stub (Signature + body/return) + Documentation
 *    2. Example test
 *    3. inventory & template (Work forwards with what we can do with the input we have?)
 *    4. Implement - work backwards from output cases, local variables/helper functions
 *    5. Sufficient Testing and Debugging
 *************************************** */

/* ==========================================
 *  Example - 1
 *   We want to represent how a student gets from their dorm/apartment to an 8am class.
 *   There are three ways: on foot, by bike, or the bus.
 * ========================================== */

/* ==========================================
 *  1st attempt ; bad approach.
  data class Commute(val kind : String, val distance : Double, val stopsAway:Int, val hasHelmet : Boolean, val minutesLate : Int)
         * What is the use of helmet on a bus?
         * How late is a walk running today?
         * Why do I care about the number of stops if I am biking or walking?
         * What stops Commute("skatebrd", -4.0, true, 0) from being constructed?
     Every commute needs SOME of these fields, and no commute needs ALL of them.
 * ========================================== */

/* ==========================================
 * 2nd attempt ; better approach.
 *  A commute is not one thing with many fields.  It is ONE OF several different things.
 * That is a sum type: in Kotlin, a sealed interface.
 */

/** Describes every way a student gets from their apartment to an 8am class */
sealed interface Commute {
    /** On foot, the healthy way
     * @property distance how far the apartment is from class, in miles
     * @property hillsClimbed how many Worcester hills are in the way
     */
    data class Walk(val distance : Double, val hillsClimbed : Int) : Commute

    /** By bike; downhill fast, uphill torture
     * @property distance how far the apartment is from class, in miles
     * @property hasHelmet true if the rider owns a helmet and can find it this morning
     */
    data class Bike(val distance : Double, val hasHelmet : Boolean) : Commute

    /** By the campus bus, in theory
     * @property stopsAway how many stops before the academic buildings
     * @property minutesLate how far behind schedule the bus is running today
     */
    data class Bus(val stopsAway : Int, val minutesLate : Int) : Commute
}

typealias Walk = Commute.Walk
typealias Bike = Commute.Bike
typealias Bus = Commute.Bus

val joesCommute = Walk(0.8, 2)
val alisCommute = Bike(0.8, true)
val anandsCommute = Bus(5, 5)
/* ========================================== */

// Constants we will use in functions
val WALK_SPEED = 20.0
val BIKE_SPEED = 10.0
val BUS_TRAVEL_TIME = 2.0
val HILL_DELAY = 4.0
val HELMET_DELAY = 2.0
val BUS_SNOW_DELAY = 20

//*************************************************
// Four things we can do with a sum type:
// - define a new data type with a sealed interface (like above)
// - construct a value by choosing ONE of the cases
// - ask which case we have with the "is" keyword
// - take the cases apart with an exhaustive when   <- the template below

//    // Template - every function that consumes a Commute starts here
//    when (aCommute) {
//        is Walk    -> ...
//        is Bike    -> ...
//        is Bus -> ...
//    }
// Notice: no else branch.  The compiler knows the three cases are ALL of them.

// Recall HtDF Steps: 1. stub+docs, 2. example test, 3. inventory & template, 4. implementation, 5. testing/debugging

fun main() {
    val anyCommute : Commute = joesCommute
    println(anyCommute)
    println(anyCommute is Bike)   // is keyword -  Kotlin's type-check operator. It asks, at runtime, "is the value in anyCommute actually a Bike?"

    // Problem - 1
    minutesToClass(joesCommute) shouldBe (24.0 plusOrMinus 0.01)
    minutesToClass(alisCommute) shouldBe (0.8*BIKE_SPEED + HELMET_DELAY plusOrMinus 0.01)
    minutesToClass(anandsCommute) shouldBe (5*BUS_TRAVEL_TIME + 10 plusOrMinus 0.01)


    // Problem - 2
    willBeLate(joesCommute, 24) shouldBe false


    // Problem - 3
    adjustToSnow(joesCommute) shouldBe Walk(joesCommute.distance, joesCommute.hillsClimbed*2)

}

/* ==========================================
 * | STUBS
 * ========================================== */

// Problem - 1 : consume a sum type, produce a Double
/** minutesToClass : Estimates how long the commute takes.
 *      * walking takes 20 minutes a mile, plus 4 minutes a hill
 *      * biking takes 10 minutes a mile, plus 2 minutes to find the helmet
 *      * the bus takes 2 minutes a stop, plus however late it is, plus 5 minutes at the stop
 * @param aCommute how the student is getting to class
 * @return the number of minutes the commute takes
 */
fun minutesToClass(aCommute : Commute) : Double {
    // Inventory: aCommute.distance  .hillsClimbed  .hasHelmet  .stopsAway  .minutesLate  *  +
    return when (aCommute) {
        is Walk -> aCommute.distance * WALK_SPEED + aCommute.hillsClimbed * HILL_DELAY
        is Bike -> aCommute.distance * BIKE_SPEED + (if (aCommute.hasHelmet) HELMET_DELAY else 0.0)
        is Bus -> aCommute.stopsAway * BUS_TRAVEL_TIME + aCommute.minutesLate + 5
    }
}

// Problem - 2 :  function composition, no when needed
/** willBeLate : Checks whether the student misses the start of class
 * @param aCommute how the student is getting to class
 * @param minutesUntilClass how many minutes are left before class starts
 * @return true if the commute takes longer than the time left
 */
fun willBeLate(aCommute : Commute, minutesUntilClass : Int) : Boolean {
    // Inventory: minutesToClass(...)  minutesUntilClass  >
    return minutesToClass(aCommute) > minutesUntilClass
}

// Problem - 3 : consume a sum type AND produce a sum type
/** adjustToSnow : Adjusts a commute for a Worcester snow day.
 *      * walks get twice as hilly, because everything is ice now
 *      * bikes stop being bikes; you push it and walk up one hill
 *      * the bus runs 20 minutes later than it already was
 * @param aCommute the commute as planned
 * @return the commute as it actually happens in the snow
 */
fun adjustToSnow(aCommute : Commute) : Commute {
    // Inventory:   Walk(...)  Bike(...)  Bus(...)
    // aCommute.distance  .hillsClimbed  .hasHelmet  .stopsAway  .minutesLate  *  +
    return when (aCommute) {
        is Walk -> Walk(aCommute.distance, aCommute.hillsClimbed*2)
        is Bike -> Walk(aCommute.distance, 1)
        is Bus -> Bus(aCommute.stopsAway, aCommute.minutesLate+BUS_SNOW_DELAY)
    }
}

