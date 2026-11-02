package buri.sc.y26.q04

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(77, 1)
        assertRun(4818, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(4, 1)
        assertRun(8008, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        var sum = 0L
        val incorrect = mutableListOf<String>()
        for (line in input) { 
            val barIndex = line.lastIndexOf("|")
            val left = line.substring(0, barIndex + 1)
            val checksum = line.substring(barIndex + 1).toInt()
            val xorResult = left.toList().map { it.code }.reduce { acc, i -> acc xor i }
            if (xorResult != checksum) {
                sum += checksum    
                incorrect.add(line)
            }
        }        
        if (part.isOne()) {
            return sum
        } 
        // Part 2
        val counts = mutableMapOf<String, Int>()
        for (line in incorrect) {
            for (command in line.split("|")[1].chunked(2)) {
                if (command !in counts.keys) {
                    counts[command] = 0
                }
                counts[command] = counts[command]!! + 1 
            }
        }
        val sortedCounts = counts.toList().sortedByDescending { it.second }.toMap().map { it.value }
        return sortedCounts[0] * sortedCounts[1]
    }
}