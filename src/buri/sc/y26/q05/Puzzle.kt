package buri.sc.y26.q05

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import buri.sc.common.position.Grid
import buri.sc.common.position.Point2D
import buri.sc.common.position.getNeighbors
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(16, 1)
        assertRun(12, 2)
        assertRun(1434, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(36, 1)
        assertRun(18, 2)
        assertRun(2008, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val rectangles = mutableListOf<List<Point2D<Int>>>()
        for (line in input) {
            rectangles.add(line.split(" ").map { Point2D(it.split(",")[0].toInt(), it.split(",")[1].toInt()) })
        }
        val maxGridX = rectangles.flatten().maxOf { it.x }
        val maxGridY = rectangles.flatten().maxOf { it.y }
        val grid = Grid(maxGridX + 1, maxGridY + 1, '.')
        for (rectangle in rectangles) {
            val minX = rectangle.minOf { it.x }
            val maxX = rectangle.maxOf { it.x }
            val minY = rectangle.minOf { it.y }
            val maxY = rectangle.maxOf { it.y }
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    grid[Point2D(x, y)] = '#'
                }
            }
        }
        val outerPoints = grid.filter { it == '#' }.filter { grid.isOuter(it) }
        if (part.isOne()) {
            return outerPoints.size
        }

        // Part 2

        // Separate the boundaries into individual polygons
        val ungroupedPoints = outerPoints.toMutableList()
        val groupedPoints = mutableListOf<Set<Point2D<Int>>>()
        while (ungroupedPoints.isNotEmpty()) {
            val point = ungroupedPoints.removeFirst()
            val group = mutableSetOf<Point2D<Int>>()
            group.add(point)

            var next = point.getNeighbors().firstOrNull { it in ungroupedPoints }
            while (next != null) {
                ungroupedPoints.remove(next)
                group.add(next)
                next = next.getNeighbors().firstOrNull { it in ungroupedPoints }
            }
            groupedPoints.add(group)
        }

        // Flatten the polygons into their upper-left point and steps around the outside.
        val origin = Point2D(0, 0)
        val reducedPolygons = mutableMapOf<Point2D<Int>, Int>()
        for (group in groupedPoints) {
            val start = group.minOf { it }
            reducedPolygons[start] = group.size
        }

        // Find the shortest path
        val points = reducedPolygons.keys.toMutableList()
        points.add(0, origin)
        return getShortestPath(points) + reducedPolygons.values.sum()
    }

    fun Grid<Char>.isOuter(point: Point2D<Int>): Boolean {
        val isEdge =
            point.x == this.xRange.first || point.x == this.xRange.last || point.y == this.yRange.first || point.y == this.yRange.last
        val isBoundary = this.getNeighbors(point, true).any { this[it] == '.' }
        return (isEdge || isBoundary)
    }

    fun getShortestPath(points: List<Point2D<Int>>): Long {
        if (points.isEmpty() || points.size == 1) {
            return 0
        }

        val numPoints = points.size
        // 2D array of pre-calculated distances between all points
        val distances = List(numPoints) { i -> List(numPoints) { j -> points[i].getManhattanDistance(points[j]) } }

        // Calculate initial lower bound: sum of two smallest edges for each node divided by 2
        val initialBound = (0..<numPoints).sumOf { rowIndex ->
            val (first, second) = getTwoSmallest(distances, rowIndex)
            first + second
        } / 2
        val initialVisited = List(numPoints) { it == 0 }
        val initialState = State(0, initialBound, 0, initialVisited, listOf(0))
        return branchAndBound(numPoints, distances, listOf(initialState), emptyList(), Long.MAX_VALUE)
    }

    fun getTwoSmallest(distances: List<List<Long>>, rowIndex: Int): Pair<Long, Long> {
        val row = distances[rowIndex]
        val sortedPoints = row.mapIndexed { index, distance ->
            if (index == rowIndex) {
                Long.MAX_VALUE
            } else {
                distance
            }
        }.sorted()
        return Pair(sortedPoints[0], sortedPoints[1])
    }

    tailrec fun branchAndBound(
        numPoints: Int,
        distances: List<List<Long>>,
        frontier: List<State>,
        bestPath: List<Int>,
        bestCost: Long
    ): Long {
        if (frontier.isEmpty()) {
            return bestCost
        }

        val sortedFrontier = frontier.sortedBy { it.bound }
        val current = sortedFrontier.first()
        val remaining = sortedFrontier.drop(1)

        // Don't process the current state if its bound is already worse than our best cost.
        if (current.bound >= bestCost) {
            return branchAndBound(numPoints, distances, remaining, bestPath, bestCost)
        }
        val currentLevel = current.path.size

        // Base Case: Return to index 0 if all points visited.
        if (currentLevel == numPoints) {
            val totalCost = current.costSoFar + distances[current.pointIndex][0]
            return if (totalCost < bestCost) {
                branchAndBound(numPoints, distances, remaining, current.path + 0, totalCost)
            } else {
                branchAndBound(numPoints, distances, remaining, bestPath, bestCost)
            }
        }

        // Create child states for all unvisited points.
        val nextChildren = (0..<numPoints).filter { !current.visited[it] }.map { nextIndex ->
            val costToAdd = distances[current.pointIndex][nextIndex]
            val nextCost = current.costSoFar + costToAdd

            val (current1, current2) = getTwoSmallest(distances, current.pointIndex)
            val currentForBound = if (currentLevel == 1) {
                current1
            } else {
                current2
            }
            val (nextForBound, _) = getTwoSmallest(distances, nextIndex)
            val nextBound = current.bound + costToAdd - ((currentForBound + nextForBound) / 2)
            val updatedVisited = current.visited.mapIndexed { index, rowIndex -> index == nextIndex || rowIndex }

            State(nextIndex, nextBound, nextCost, updatedVisited, current.path + nextIndex)
        }.filter { it.bound < bestCost }
        // Optimize by removing any that won't beat best cost.           

        return branchAndBound(numPoints, distances, remaining + nextChildren, bestPath, bestCost)
    }
}

data class State(
    val pointIndex: Int,
    val bound: Long,
    val costSoFar: Long,
    val visited: List<Boolean>,
    val path: List<Int>
)