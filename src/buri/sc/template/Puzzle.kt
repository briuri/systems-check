package buri.sc.template

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
        assertRun(0, 1)
        assertRun(0, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(0, 1)
        assertRun(0, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        for (line in input) {
            
        }        
        if (part.isOne()) {
            return -1
        } 
        // Part 2
        return -1
    }
}