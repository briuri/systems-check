package buri.sc.y26.q12

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import buri.sc.common.Pathfinder
import buri.sc.common.countSteps
import buri.sc.common.position.Point3D
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
        assertRun(45, 1)
        assertRun(8055, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(19, 1)
        assertRun(230, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(10, 1)
        assertRun(115, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val walls = mutableSetOf<Point3D<Int>>()
        for (data in input[0].split(":")) {
            val numbers = data.split(",").map { it.toInt() }
            walls.add(Point3D(numbers[0], numbers[1], numbers[2]))
        }
        val origin = Point3D(0, 0, 0)
        val pathfinder = Pathfinder<Point3D<Int>> { current ->
            val neighbors = mutableSetOf<Point3D<Int>>()
            neighbors.add(Point3D(current.x + 1, current.y - 1, current.z))
            neighbors.add(Point3D(current.x - 1, current.y + 1, current.z))
            neighbors.add(Point3D(current.x + 1, current.y, current.z - 1))
            neighbors.add(Point3D(current.x, current.y + 1, current.z - 1))
            neighbors.add(Point3D(current.x, current.y - 1, current.z + 1))
            neighbors.add(Point3D(current.x - 1, current.y, current.z + 1))
            neighbors.filter { it !in walls }
        }
        var cameFrom = pathfinder.exploreFrom(origin)
        if (part.isOne()) {
            return cameFrom.keys.size
        }

        val allCells = cameFrom.keys
        var maxDistance = Int.MIN_VALUE
        for (cell1 in allCells) {
            cameFrom = pathfinder.exploreFrom(cell1)
            for (cell2 in allCells.filter { it != cell1 }) {
                val distance = cameFrom.countSteps(cell1, cell2)
                if (distance > maxDistance) {
                    maxDistance = distance
                }
            }
        }
        if (part.isTwo()) {
            return maxDistance
        }
        return (maxDistance / 2.0).roundToInt()
    }
}