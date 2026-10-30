package buri.sc.y26.q02

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import buri.sc.common.Pathfinder
import buri.sc.common.countSteps
import buri.sc.common.position.Grid
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
        assertRun(32, 1)
        assertRun(346, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(50, 1)
        assertRun(467, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val start = Point2D(0, 0)
        val end = grid.filter { it == '*' }.first()

        for (y in grid.yRange) {
            for (x in grid.xRange) {
                val point = Point2D(x, y)
                if (point == start) {
                    grid[point] = '0'   // Ignore cost for starting square
                }
                if (grid[point] in listOf('.', '*')) {
                    val neighbors = grid.getNeighbors(point, true)
                    grid[point] = if (neighbors.any { grid[it] == '#' }) {
                        '2'
                    } else {
                        '1'
                    }
                }
            }
        }

        if (part.isOne()) {
            val pathfinder = Pathfinder<Point2D<Int>> { current ->
                grid.getNeighbors(current).filter { grid[it] != '#' }
            }
            val cameFrom = pathfinder.exploreFrom(start)
            return cameFrom.countSteps(start, end)
        }

        val minCostSoFar = mutableMapOf<Point2D<Int>, Int>()
        val frontier = mutableListOf<State>()
        frontier.add(State(start, 0))
        while (frontier.isNotEmpty()) {
            val current = frontier.removeFirst()
            val cost = grid[current.point].digitToInt()
            if (current.point !in minCostSoFar.keys || minCostSoFar[current.point]!! > current.costSoFar + cost) {
                minCostSoFar[current.point] = current.costSoFar + cost
                for (next in grid.getNeighbors(current.point).filter { grid[it] != '#' }) {
                    frontier.add(State(next, current.costSoFar + cost))
                }
            }
            frontier.sortBy { it.costSoFar }
        }
        return minCostSoFar[end]!!
    }
}

data class State(val point: Point2D<Int>, val costSoFar: Int)