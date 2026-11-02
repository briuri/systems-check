package buri.sc.y26.q08

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import buri.sc.common.extractInts
import org.junit.Test
import kotlin.math.roundToInt

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(171, 1)
        assertRun(681, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(403, 1)
        assertRun(787, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val edges = input.subList(0, input.indexOf("")).map { Edge(it) }
        for (line in input.subList(input.indexOf("") + 1, input.size)) {
            val tokens = line.split(" ")
            edges.firstOrNull { it.id == tokens[0] }?.rubbish = tokens[1].toInt()
        }
        val starts = edges.filter { it.id !in edges.map { it2 -> it2.next } }
        var states = mutableMapOf<String, State>()
        starts.forEach { states[it.id] = State(it.id,0)}
        while (!states.all { it.value.id == "inc" }) {
            val nextStates = states.toMutableMap()
            for (beacon in states.keys) {
                val state = states[beacon]!!
                val edge = edges.firstOrNull { it.id == state.id }
                if (edge != null) {
                    var rubbish = if (part.isTwo() && state.time < edge.clearedAt ) { edge.rubbish } else { 0 }
                    if (rubbish > 0) {
                        val beaconsHere = states.count { it.value == state }
                        rubbish = ((1.0 * rubbish) / beaconsHere).roundToInt()
                        edge.clearedAt = state.time + 1
                    }
                    nextStates[beacon] = State(edge.next, state.time + edge.weight + rubbish)
                }
            }
            states = nextStates
        }
        return states.minOf { it.value.time }
    }
}

data class Edge(val line: String) {
    val id = line.split("-")[0]
    val next = line.split(">")[1]
    val weight = line.extractInts(allowNegative = false)[0]
    var rubbish = 0
    var clearedAt = Int.MAX_VALUE
}
data class State(val id: String, val time: Int)