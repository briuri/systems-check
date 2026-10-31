package buri.sc.y26.q03

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import buri.sc.common.position.Point2D
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(1, 1)
        assertRun(4, 2)
        assertRun(29120, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(75, 1)
        assertRun(168, 2)
        assertRun(24760, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val rawNodes = input.subList(0, input.indexOf(""))
        val message = input.last().split(",").map { it.toLong() }
        val nodes = mutableSetOf<Node>()
        for (rawNode in rawNodes) {
            val tokens = rawNode.split(" ")
            val id = tokens[0].drop(1)
            val pos = Point2D(tokens[1].drop(1).toInt(), tokens[2].drop(1).toInt())
            nodes.add(Node(id, pos))
        }

        if (part.isOne()) {
            val paths = nodes.getPaths("S", "D")
            return paths.size
        }
        // Part 2
        val startNode = nodes.first { it.id == "S" }
        message.forEach { startNode.addLast(it) }

        while (true) {
            val current = nodes.filter { !it.dead && it.queueSize() > 0 }.minBy { it.priority() }
            if (current.id == "D") {
                break
            }
            val targets = nodes.filter { it != current && current.distanceFrom(it) <= 5 }
            val sortedTargets =
                targets.sortedWith(compareBy({ current.distanceFrom(it) }, { it.queueSize() }, { it.priority() }))
                    .toMutableList()

            while (current.queueSize() > 0) {
                val chunk = current.peekFirst()
                if (targets.none { it.accepts(chunk) }) {
                    current.dead = true
                    break
                }
                val next = sortedTargets.removeFirst()
                if (next.accepts(chunk)) {
                    next.addLast(chunk)
                    current.removeFirst()
                }
                sortedTargets.add(next)
            }
        }
        return nodes.first { it.id == "D" }.checksum()
    }

    fun Set<Node>.getPaths(
        start: String,
        end: String,
        path: MutableList<String> = mutableListOf()
    ): List<List<String>> {
        path.add(start)
        if (start == end) {
            return listOf(path)
        }
        val paths = mutableListOf<List<String>>()
        val startNode = this.first { it.id == start }
        for ((id) in this.filter { it.id !in path && startNode.distanceFrom(it) <= 5 }) {
            paths.addAll(getPaths(id, end, path.toMutableList()))
        }
        path.removeAt(path.size - 1)
        return paths
    }
}

data class Node(val id: String, val pos: Point2D<Int>) {
    private val visited = mutableSetOf<Long>()
    private val queue = mutableListOf<Long>()
    var dead: Boolean = false

    fun priority(): Int {
        return when (id) {
            "S" -> 0
            "D" -> Int.MAX_VALUE
            else -> id.toInt()
        }
    }

    fun distanceFrom(other: Node): Long = pos.getManhattanDistance(other.pos)
    fun accepts(chunk: Long): Boolean = chunk !in visited
    fun queueSize(): Int = queue.size

    fun addLast(chunk: Long) {
        if (chunk !in visited) {
            visited.add(chunk)
            queue.add(chunk)
        }
    }

    fun peekFirst(): Long {
        return queue.first()
    }

    fun removeFirst() {
        queue.removeFirst()
    }

    fun checksum(): Long {
        return queue.takeLast(5).sum() * queue.size
    }

    override fun toString(): String = "$id: $queue"
}