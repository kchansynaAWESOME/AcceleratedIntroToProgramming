/* ==========================================
 * Kingston Chansyna, Krista Sebastian
 * ========================================== */

package Labs

import io.kotest.matchers.shouldBe

// Data Definitions

/**
 * A widget in the inventory.
 *
 * @property name the name of the item
 * @property quantity how many are in stock
 * @property time how long it takes to produce. A value of 0 means this is a base component and
 *   cannot be made by our factory
 * @property price how much it sells for
 * @property parts the subwidgets needed to construct this item
 */
data class Widget(val name : String,  val quantity: Int,  val time:Int,  val price: Int, val parts : WidgetList)

/**
 * A list of [Widget]s
 */
sealed interface WidgetList {
    data object Empty : WidgetList
    data class Node(val first : Widget, val rest : WidgetList) : WidgetList
}

typealias WEmpty = WidgetList.Empty
typealias WNode = WidgetList.Node


/**
 * A widget in the inventory, the same as [Widget] except that its subwidgets are stored in Kotlin's
 * built-in [List] instead of our user defined [WidgetList].
 *
 * @property name the name of the item
 * @property quantity how many are in stock
 * @property time how long it takes to produce. A value of 0 means this is a base component and cannot be made by our factory
 * @property price how much it sells for
 * @property parts the subwidgets needed to construct this item
 */
data class NewWidget(val name : String,  val quantity: Int,  val time:Int,  val price: Int, val parts : List<NewWidget>)

/* ==========================================
 * | Helpers for working with WidgetList
 * ========================================== */

/**
 * Returns the first widget in [widgets].
 *
 * @param widgets the list to look at
 * @return the widget at the front of the list
 * @throws NoSuchElementException if [widgets] is empty
 */
fun getFirst (widgets : WidgetList): Widget {
    return when (widgets) {
        is WEmpty -> throw kotlin.NoSuchElementException("List is empty!")
        is WNode ->  { widgets.first }
    }
}

/**
 * Adds [widget] to the front of [widgets].
 *
 * @param widget the widget to add
 * @param widgets the list to add to
 * @return a new list with [widget] first, followed by [widgets]
 */
fun addFirst (widget: Widget, widgets : WidgetList): WidgetList {
    return WNode(widget, widgets)
}

/**
 * Returns everything in [widgets] after the first widget.
 *
 * @param widgets the list to look at
 * @return the list without its first widget
 * @throws NoSuchElementException if [widgets] is empty
 */
fun getRest (widgets : WidgetList): WidgetList {
    return when (widgets) {
        is WEmpty -> throw kotlin.NoSuchElementException("List is empty!")
        is WNode ->  { widgets.rest }
    }
}

/**
 * Joins two lists together, end to end.
 *
 * @param widgets1 the list that comes first
 * @param widgets2 the list that comes second
 * @return a list holding every widget of [widgets1] followed by every widget of [widgets2]
 */
fun appendWlists (widgets1 : WidgetList, widgets2 : WidgetList): WidgetList {
    return when (widgets1) {
        is WEmpty -> widgets2
        is WNode ->  { WNode(widgets1.first, appendWlists(widgets1.rest, widgets2)) }
    }
}

/**
 * Counts the widgets in [widgets].
 * @param widgets the list to count
 * @return how many widgets are in the list
 */
fun lengthWlist (widgets : WidgetList) : Int {
    return when (widgets){
        is WEmpty -> 0
        is WNode -> 1 + lengthWlist(widgets.rest)
    }
}

/* ==========================================
 * | Templates
 * ========================================== */
/*

// function 1
fun findQuantityOver(widget:Widget,numOver:Int): ... {
    return if (widget.quantity > numOver) {
        findQuantityOverLOW(WNode(widget,widget.parts))
    } else {
        findQuantityOverLOW(widget.parts,numOver)
    }
}

// stands for ListOfWidgets 😊😊
// function 1 helper
fun findQuantityOverLOW(widgetList:WidgetList,numOver:Int):... {
    return when (widgetList) {
        is WEmpty -> ...
        is WNode ->
            findQuantityOver(widgetList.first,numOver)
            findQuantityOverLOW(widgetList.rest,numOver)
    }
}

// function 2
fun findCheaperThan(widget:Widget,numUnder:Int): ... {
    return if (widget.price < numUnder) {
        findQuantityOverLOW(WNode(widget,widget.parts))
    } else {
        ...
    }
}

// function 2 helper
fun findPriceUnderLOW(widgetList:WidgetList,priceUnder:Int):... {
    return when (widgetList) {
        is WEmpty -> ...
        is WNode ->
            findCheaperThan(widgetList.first,priceUnder)
            findPriceUnderLOW(widgetList.rest,priceUnder))
    }
}

// function 3
fun findHardMake(widget:Widget,quantityNum:Int,priceNum:Int): ... {
    return if (widget.quantity < quantityNum || widget.price > priceNum) {
        findQuantityOverLOW(WNode(widget,widget.parts))
    } else {
        findQuantityOverLOW(widget.parts,quantityUnder,priceUnder)
    }
}

// function 3 helper
fun findHardMakeLOW(widgetList:WidgetList,quantityUnder:Int,priceUnder:Int):... {
    return when (widgetList) {
        is WEmpty -> ...
        is WNode ->
            findQuantityOver(widgetList.first,numOver)
            findQuantityOverLOW(widgetList.rest,numOver)
    }
}

// function 4
fun resupplyWidget(widget:Widget,cutoff:Int,increase:Int): ... {
    return if (widget.quantity < cutoff) {
        widget.copy(
        quantity = widget.quantity + increase,
        parts = resupplyWidgetLOW(widgetList.parts,cutoff,increase)
        )
    } else {
        ...
    }
}

// function 4 helper
fun resupplyWidgetLOW(widgetList:WidgetList,cutoff:Int,increase:Int): ... {
    return when (widgetList) {
        WEmpty -> ...
        WNode ->
            resupplyWidget(widgetList.first,cutoff,increase)
            resupplyWidgetLOW(widgetList.rest,cutoff,increase)
    }
}

*/

/* ==========================================
 * | PART - 1 - Mutual recursion in a warehouse of widgets
 * ========================================== */
// Task-1  : findQuantityOver

/** findQuantityOver: consumes a [Widget] and an [Int] and returns a [WidgetList]
 *  It examines the "widget", as well as all the sub-widgets used to manufacture it, and returns all whose quantity in stock is greater than [numOver].
 * @param widget the [Widget] to be filtered
 * @param numOver the boundary which the widget's quantity must be greater than
 * @return a [WidgetList] containing all widgets in the provided [widget] and it's parts that have a quantity over [numOver]
 */
fun findQuantityOver(widget:Widget,numOver:Int): WidgetList {
    return if (widget.quantity > numOver) {
        WNode(widget,findQuantityOverLOW(widget.parts,numOver))
    } else {
        findQuantityOverLOW(widget.parts,numOver)
    }
}


/** findQuantityOverLOW: consumes a [WidgetList] and an [Int] and returns a [WidgetList]
 *  It examines the [widgetList] and returns all whose quantity in stock is greater than [numOver].
 * @param widgetList the [WidgetList] to be filtered
 * @param numOver the boundary which the widgets' quantity must be greater than
 * @return a [WidgetList] containing all widgets in the provided [widgetList] that have a quantity over [numOver]
 */
// stands for ListOfWidgets 😊😊
fun findQuantityOverLOW(widgetList:WidgetList,numOver:Int):WidgetList {
    return when (widgetList) {
        is WEmpty -> WEmpty
        is WNode -> appendWlists(findQuantityOver(widgetList.first,numOver), findQuantityOverLOW(widgetList.rest,numOver))
}}

// Task - 2 : findCheaperThan

/** findCheaperThan: consumes a [Widget] and an [Int] and returns a [WidgetList].
 * It examines the [widget], as well as all the sub-widgets used to manufacture it, and returns those whose price is less than [priceUnder].
 * @param widget the [Widget] to be filtered
 * @param priceUnder the boundary in which the widget's price must be less than
 * @return a [WidgetList] of the widget and its sub-widgets whose price is less than [priceUnder]
 */
fun findCheaperThan(widget:Widget,priceUnder:Int): WidgetList {
    return if (widget.price < priceUnder) {
        WNode(widget,findPriceUnderLOW(widget.parts,priceUnder))
    } else {
        findPriceUnderLOW(widget.parts,priceUnder)
    }
}

/** findPriceUnderLOW: consumes a [WidgetList] and an [Int] and returns a [WidgetList].
 * It examines the [widgetList] and returns a new list with the widgets with a price less than [priceUnder].
 * @param widgetList the [WidgetList] to be filtered
 * @param priceUnder the boundary which the widgets' price must be less than
 * @return a [WidgetList] of the widgets with prices less than [priceUnder]
 */
fun findPriceUnderLOW(widgetList:WidgetList,priceUnder:Int):WidgetList {
    return when (widgetList) {
        is WEmpty -> WEmpty
        is WNode -> appendWlists(findCheaperThan(widgetList.first,priceUnder), findPriceUnderLOW(widgetList.rest,priceUnder))
    }
}

// Task-3 : findHardMake

/** findHardMake: consumes a [Widget], an [Int] , and another [Int], and returns a [WidgetList].
 * It examines the [widget], as well as all the sub-widgets used to manufacture it, and returns those whose quantity in stock is less than [quantityNum] or whose cost is greater than [priceNum].
 * @param widget the [Widget] being filtered for its quantity and price
 * @param quantityNum the boundary in which the widget's quantity must be less than
 * @param priceNum the boundary in which the widget's price must be greater than
 * @return a [WidgetList] of the widgets whose quantity in stock is less than [quantityNum] or whose cost is greater than [priceNum].
 */
fun findHardMake(widget:Widget,quantityNum:Int,priceNum:Int): WidgetList {
    return if (widget.quantity < quantityNum || widget.price > priceNum) {
        WNode(widget,findHardMakeLOW(widget.parts,quantityNum,priceNum))
    } else {
        findHardMakeLOW(widget.parts,quantityNum,priceNum)
    }
}

/** findHardMakeLOW: consumes a [WidgetList], an [Int] , and another [Int], and returns a [WidgetList].
 * It examines the [widgetList], as well as all the sub-widgets used to manufacture it, and returns those whose quantity in stock is less than [quantityNum] or whose cost is greater than [priceNum].
 * @param widgetList the [WidgetList] being filtered for its quantity and price
 * @param quantityNum the boundary in which the widget's quantity must be less than
 * @param priceNum the boundary in which the widget's price must be greater than
 * @return a [WidgetList] of the widgets whose quantity in stock is less than [quantityNum] or whose cost is greater than [priceNum].
 */
fun findHardMakeLOW(widgetList:WidgetList,quantityNum:Int,priceNum:Int):WidgetList {
    return when (widgetList) {
        is WEmpty -> WEmpty
        is WNode -> appendWlists(findHardMake(widgetList.first, quantityNum, priceNum), findHardMakeLOW(widgetList.rest,quantityNum,priceNum))
    }
}

// Task - 4 : resupplyWidget

/** resupplyWidget: consumes a [Widget], an [Int], and an [Int], and returns a [Widget].
 * reSupplyWidget ensures an adequate supply.
 * It returns a copy of the given widget, but with every widget in the tree (the widget itself and all of its sub-widgets, at any depth) are restocked if their quantities were strictly less than quantity.
 * @param widget the [Widget] being supplied
 * @param cutoff the threshold beneath which the [widget]'s quantity will be restocked.
 * @param increase the amount of stock resupplied if the [widget]'s quantity did not reach [cutoff]
 * @return a new [Widget] with itself and its sub-widgets restocked.
 */
fun resupplyWidget(widget:Widget,cutoff:Int,increase:Int): Widget {
    return if (widget.quantity < cutoff) {
        widget.copy(
            quantity = widget.quantity + increase,
            parts = resupplyWidgetLOW(widget.parts,cutoff,increase)
        )
    } else {
        widget.copy(
            parts = resupplyWidgetLOW(widget.parts,cutoff,increase)
        )
    }
}

/** resupplyWidgetLOW: helps resupplies widgets in [widgetList] through recursion
 * @param widgetList the [WidgetList] being recursed and restocked
 * @param cutoff the threshold beneath which each [Widget] in [widgetList] will be restocked.
 * @param increase the amount of stock resupplied for each [Widget] beneath the [cutoff] in [widgetList]
 * @return a [WidgetList] with its widgets restocked
 */
fun resupplyWidgetLOW(widgetList:WidgetList,cutoff:Int,increase:Int): WidgetList {
    return when (widgetList) {
        is WEmpty -> WEmpty
        is WNode -> WNode(resupplyWidget(widgetList.first,cutoff,increase),resupplyWidgetLOW(widgetList.rest,cutoff,increase))
    }
}


/* ==========================================
 * | PART 2 - Generalizing your code
 * ========================================== */
// Task - 5 : filterWidget

// Task - 6 : findQuantityOver2,  findCheaperThan2, and  findHardMake2 using filterWidget

// Task - 7 : findGoodMake, findToughAdvertise using filterWidget


/* ==========================================
 * | PART 3 - Finding the widget with the extreme value
 * ========================================== */
// Task - 8 : findExtreme

// Task - 9 : maxYield

/* ==========================================
 * | Part 4:  Translating to Kotlin's built-in List
 * ========================================== */
// Task - 10 : newFilterWidget

// Task - 11 : newFindExtreme

// Task - 12 : newMaxYield

// Tests for HW
val wire = Widget("Wire", 3, 0, 3, WEmpty)
val cord = Widget("Cord", 7, 0, 5, WNode(wire, WEmpty))
val numbers = Widget("Numbers", 9, 0, 5, WEmpty)
val buttons = Widget("Buttons", 8, 5, 5, WNode(numbers, WEmpty))
val receiver = Widget("Receiver", 11, 0, 7, WEmpty)
val telephone = Widget("Telephone", 5, 20, 15, WNode(receiver, WNode(buttons, WNode(cord, WEmpty))))

/** runTests: runs tests!!
 */
private fun runTests() {
    // function 1
    findQuantityOver(wire,2) shouldBe WNode(wire,WEmpty)
    findQuantityOver(cord,4) shouldBe WNode(cord,WEmpty)
    findQuantityOver(cord,-999) shouldBe WNode(cord,WNode(wire,WEmpty))
    findQuantityOver(telephone,999) shouldBe WEmpty

    //function 2
    findCheaperThan(wire,4) shouldBe WNode(wire,WEmpty)
    findCheaperThan(cord,4) shouldBe WNode(wire,WEmpty)
    findCheaperThan(telephone,-999) shouldBe WEmpty
    findCheaperThan(buttons,999) shouldBe WNode(buttons,WNode(numbers,WEmpty))

    //function 3
    findHardMake(wire,4,4) shouldBe WNode(wire,WEmpty)
    findHardMake(cord,7,5) shouldBe WNode(wire,WEmpty)
    findHardMake(telephone,-99999999,99999) shouldBe WEmpty
    findHardMake(buttons,9999999,-999999) shouldBe WNode(buttons,WNode(numbers,WEmpty))

    //function 4
    resupplyWidget(wire,4,20) shouldBe Widget("Wire", 23, 0, 3, WEmpty)
    resupplyWidget(cord,-99999,-10) shouldBe cord
    resupplyWidget(numbers,9,329324890) shouldBe numbers
    resupplyWidget(buttons,9,-1000) shouldBe Widget("Buttons", 8-1000, 5, 5, WNode(numbers, WEmpty))

    println("😂😂😂😂")
}

fun main() {
    runTests()

}
