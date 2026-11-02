package buri.sc.y26.q07

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import buri.sc.common.extractInts
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(374, 1)
        assertRun(80010, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(234, 1)
        assertRun(26194, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        if (part.isOne()) {
            var sum = 0
            for (line in input) {
                val numbers = line.extractInts()
                val efficiency = (100 * numbers[2]) / numbers[1]
                if (efficiency < 97) {
                    sum += efficiency
                }
            }
            return sum
        }

        // Part 2
        val sums = mutableMapOf<String, Int>()
        for (line in input) {
            val loads = line.split(" ")[0].split("|")[1].chunked(3)
            val numbers = line.extractInts()
            val efficiency = (100 * numbers[2]) / numbers[1]
            if (efficiency < 97) {
                for (load in loads) {
                    if (sums[load] == null) {
                        sums[load] = 0
                    }
                    sums[load] = sums[load]!! + efficiency
                }
            }
        }
        return sums.maxOf { it.value }
    }
}