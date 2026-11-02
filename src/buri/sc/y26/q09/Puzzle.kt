package buri.sc.y26.q09

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import buri.sc.common.Pathfinder
import buri.sc.common.countSteps
import buri.sc.common.position.Grid
import buri.sc.common.position.Point2D
import org.junit.Test
import kotlin.math.abs

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(19, 1)
        assertRun(195, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(25, 1)
        assertRun(263, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val map = input.map { it.replace(" ", "") }
        val grid = Grid.fromCharInput(map)
        val start = grid.filter { it == '^' }.first()
        val end = grid.filter { it == '*' }.first()
        grid[start] = '0'
        grid[end] = '0'

        if (part.isOne()) {
            val pathfinder = Pathfinder<Point2D<Int>> { current ->
                grid.getNeighbors(current, true).filter { grid.canTravel(it, current) }
            }
            val cameFrom = pathfinder.exploreFrom(start)
            return cameFrom.countSteps(start, end)
        }
        // Part 2

        val minCostSoFar = mutableMapOf<Point2D<Int>, Int>()
        minCostSoFar[end] = 0
        val frontier = mutableListOf<State>()
        frontier.add(State(end, 0))
        while (frontier.isNotEmpty()) {
            val current = frontier.removeFirst()
            for (next in grid.getNeighbors(current.point, true)
                .filter { grid.canTravel(it, current.point) }) {
                val cost = if (grid[next].digitToInt() > grid[current.point].digitToInt()) { 5 } else { 1 }
                if (minCostSoFar[next] == null || minCostSoFar[next]!! > current.costSoFar + cost) {
                    minCostSoFar[next] = current.costSoFar + cost
                    frontier.add(State(next, current.costSoFar + cost))
                }
            }
            frontier.sortBy { it.costSoFar }
        }
        return minCostSoFar[start]!!
    }
    
    fun Grid<Char>.canTravel(point1: Point2D<Int>, point2: Point2D<Int>): Boolean {
        return abs(this[point1].digitToInt() - this[point2].digitToInt()) <= 1
    }
}
data class State(val point: Point2D<Int>, val costSoFar: Int)