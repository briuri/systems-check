package buri.sc.y26.q11

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
        assertRun(96, 1)
        assertRun(8205, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(696, 1)
        assertRun(1541027578779, 0, true)
    }

    val cache = mutableMapOf<String, Long>()

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        return process(part, input[0].split(" "))
    }

    fun process(part: Part, input: List<String>, initialPointer: Int = 0, length: Int = input.size): Long {
        val key = "$initialPointer-$length"
        if (key !in cache.keys) {
            var outputSize = 0L
            var pointer = initialPointer
            while (pointer < length) {
                if (input[pointer] == "FF") {
                    outputSize += input[pointer + 2].toInt(16)
                    pointer += 3
                } else if (part.isTwo() && input[pointer] == "FE") {
                    val back = input[pointer + 1].toInt(16)
                    val length = input[pointer + 2].toInt(16)
                    outputSize += process(part, input, pointer - back, pointer - back + length)
                    pointer += 3
                } else {
                    outputSize++
                    pointer++
                }
            }
            cache[key] = outputSize
        }
        return cache[key]!!
    }
}