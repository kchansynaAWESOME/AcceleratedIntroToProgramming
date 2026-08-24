// Kingston Chansyna

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

// Reverses the given message
// @param origMessage the value of the base message
// @return the reversed version of origMessage
//fun reverseMessage(origMessage: String): String {
//    return ""
//}

// Combines two call signs into a single string
// The shorter call sign is placed before the other one, similar sizes are ambiguous
// @ param callSign1 One string call sign
// @ param callSign2 Second string call sign
// @ return The combination of the two call signs. Is "?" if both calls signs have similar lengths.
//fun mergeCallSigns(callSign1:String, callSign2:String): String {
//    return ""
//}

// Counts the number of steps it takes for n to reach 1 according to the hailstone sequence
// @param n the starting number
// @ return the number of steps it took for the sequence to lead to 1
// @throw IllegalStateException if n is negative
//fun hailstoneSequence(n: Int): Int {
//    return 0
//}

// Summarizes the message according to the source, length, and priority of the provided transmission
// @ param transmission Transmission object being summarized
// @ return Summarized message.
// @throws IllegalStateException if the transmission's priority is less than 1 or greater than 10
//fun summarizeMessage(transmission: Transmission): String {
//    return ""
//}

fun main() {
    reverseMessage("wassguuud") shouldBe "duuugssaw"
    reverseMessage("w") shouldBe "w"
    reverseMessage("") shouldBe ""

    mergeCallSigns("Big", "X") shouldBe "X" + "Big"
    mergeCallSigns("Sigma", "Delta") shouldBe "?"
    mergeCallSigns("C", "Man") shouldBe "C" + "Man"

    hailstoneSequence(7) shouldBe 16
    hailstoneSequence(8) shouldBe 3
    shouldThrow<IllegalStateException> {hailstoneSequence(-20)}

    summarizeMessage(Transmission(
        "wpi",
        3,
        "hi guys")) shouldBe "[MEDIUM] wpi: \"hi guys\""
    summarizeMessage(Transmission(
        source = "Mars Orbiter",
        priority = 2,
        message = "Solar panels deployed successfully."
    )) shouldBe "[LOW] Mars Orbiter: \"Solar panels deploye...\""
    shouldThrow<IllegalStateException> {summarizeMessage(Transmission(
        source = "place",
        priority = 13,
        message = "things are happening"
    )) }
}

data class Transmission(
    val source: String,
    val priority: Int,
    val message: String
)

// Reverses the given message
// @param origMessage the value of the base message
// @return the reversed version of origMessage
fun reverseMessage(origMessage: String): String {
    return when {
        origMessage.length > 1 -> reverseMessage(origMessage.drop(1)) + origMessage.take(1)
        else -> origMessage
    }
}

// Combines two call signs into a single string
// The shorter call sign is placed before the other one, similar sizes are ambiguous
// @ param callSign1 One string call sign
// @ param callSign2 Second string call sign
// @ return The combination of the two call signs. Is "?" if both calls signs have similar lengths.
fun mergeCallSigns(callSign1:String, callSign2:String): String {
    return when {
        callSign1.length < callSign2.length -> callSign1 + callSign2
        callSign2.length < callSign1.length -> callSign2 + callSign1
        else -> "?"
    }
}

// Counts the number of steps it takes for n to reach 1 according to the hailstone sequence
// @param n the starting number
// @ return the number of steps it took for the sequence to lead to 1
// @throw IllegalStateException if n is negative
fun hailstoneSequence(n: Int): Int {
    return when {
        n < 1 -> throw IllegalStateException("n is negative")
        n == 1 -> 0
        n % 2 == 0 -> hailstoneSequence(n / 2) + 1
        else -> hailstoneSequence((n * 3) + 1) + 1
    }
}

// Summarizes the message according to the source, length, and priority of the provided transmission
// @ param transmission Transmission object being summarized
// @ return Summarized message.
// @throws IllegalStateException if the transmission's priority is less than 1 or greater than 10
fun summarizeMessage(transmission: Transmission): String {
    return when {
        transmission.priority < 1 -> throw IllegalStateException("priority out of bounds")
        transmission.priority > 10 -> throw IllegalStateException("priority out of bounds")
        transmission.priority <= 2 -> "[LOW] "
        transmission.priority >= 6 -> "[HIGH] "
        else -> "[MEDIUM] "
    } + transmission.source + ": \"" +
            (if (transmission.message.length > 20) {transmission.message.substring(0,20) + "..."} else {transmission.message}) +
            "\""
}