package buri.sc.y26.q01

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import org.junit.Test
import kotlin.math.max

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(7, 1)
        assertRun(18165, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(3, 1)
        assertRun(0, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val events = input.map { Event(it) }
        val disableEvents = events.filter { it.isDisable }
        val enableEvents = events.filter { !it.isDisable }
        val maxTime = events.last().time

        val ranges = mutableListOf<DisabledRange>()
        for (start in disableEvents) {
            val end = enableEvents.firstOrNull { it.reactor == start.reactor && it.time >= start.time }
            val endTime = end?.time ?: maxTime
            ranges.add(DisabledRange(start.reactor, start.time, endTime))
        }
        if (part.isOne()) {
            return ranges.maxOf { it.timeEnabled - it.timeDisabled }
        }
        
        val disabledCount = mutableMapOf<Int, Int>()
        for ((_, timeDisabled, timeEnabled) in ranges) {
            for (i in timeDisabled..timeEnabled) {
                if (disabledCount[i] == null) {
                    disabledCount[i] = ranges.count { it.inRange(i) }
                    println("$i ${ranges.count { it.inRange(i) }}")
                }
            }
        }
        val lowestTimes = disabledCount.filter { it.value == disabledCount.maxOf { it2 -> it2.value } }
        return getLongestLength(lowestTimes.keys.toList())
    }

    fun getLongestLength(nums: List<Int>): Int {
        var maxLength = 1
        var currentLength = 1
        for (i in 1..<nums.size) {
            if (nums[i] == nums[i - 1] + 1) {
                currentLength++
                maxLength = maxOf(maxLength, currentLength)
            } else if (nums[i] != nums[i - 1]) {
                currentLength = 1
            }
        }
        return maxLength
    }
}

data class Event(val line: String) {
    val time = line.split(" ")[0].split("=")[1].toInt()
    val reactor = line.split(" ")[1].drop(1).toInt()
    val isDisable = line.contains("disabled")
}

data class DisabledRange(val reactor: Int, val timeDisabled: Int, val timeEnabled: Int) {
    fun getLength() = timeEnabled - timeDisabled
    fun inRange(test: Int) = (test >= timeDisabled && test < timeEnabled)
}