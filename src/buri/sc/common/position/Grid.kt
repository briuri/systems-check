package buri.sc.common.position

import buri.sc.common.extractInts
import buri.sc.common.position.Orientation.*

/**
 * Helper class for grid navigation. Can hold Ints or Chars.
 *
 * @author Brian Uri!
 */
open class Grid<T>(val width: Int, val height: Int, private val defaultValue: T) {
    private val grid = MutableList(width * height) { defaultValue }
    val xRange = 0 until width
    val yRange = 0 until height

    init {
        if (defaultValue !is Int && defaultValue !is Char) {
            throw IllegalArgumentException("Only Int and Chars can be stored in grids.")
        }
    }

    /**
     * Sums all actual numbers in the grid.
     */
    fun sum(): Int {
        var sum = 0
        for (value in grid.filter { it is Int }) {
            sum += value as Int
        }
        return sum
    }

    /**
     * Returns all points in the grid that contain some value.
     */
    fun filter(predicate: (T) -> Boolean): List<Point2D<Int>> {
        val list = mutableListOf<Point2D<Int>>()
        for (y in yRange) {
            for (x in xRange) {
                if (predicate.invoke(get(x, y))) {
                    list.add(Point2D(x, y))
                }
            }
        }
        return list
    }

    /**
     * Counts occurrences of some value.
     */
    fun count(predicate: (T) -> Boolean): Int {
        return grid.count(predicate)
    }

    /**
     * Creates a copy of this grid.
     */
    fun copy(orientation: Orientation = ORIGINAL): Grid<T> {
        val copy = when (orientation) {
            CLOCKWISE_90, CLOCKWISE_270 -> Grid(height, width, defaultValue)
            else -> Grid(width, height, defaultValue)
        }
        for (y in yRange) {
            for (x in xRange) {
                val point = when (orientation) {
                    CLOCKWISE_90 -> Point2D(height - y - 1, x)
                    CLOCKWISE_180 -> Point2D(width - x - 1, height - y - 1)
                    CLOCKWISE_270 -> Point2D(y, width - x - 1)
                    MIRROR_H -> Point2D(width - x - 1, y)
                    MIRROR_V -> Point2D(x, height - y - 1)
                    else -> Point2D(x, y)
                }
                copy[point] = get(x, y)
            }
        }
        return copy
    }

    /**
     * Returns a smaller grid from inside this grid.
     */
    fun getSubGrid(start: Point2D<Int>, newWidth: Int, newHeight: Int): Grid<T> {
        assert(isInBounds(start) && newWidth <= width && newHeight <= height)
        val grid = Grid(newWidth, newHeight, defaultValue)
        for (y in start.y until start.y + newHeight) {
            for (x in start.x until start.x + newWidth) {
                grid[x - start.x, y - start.y] = get(x, y)
            }
        }
        return grid
    }

    /**
     * Returns all neighbors of a point in bounds of the grid.
     */
    fun getNeighbors(current: Point2D<Int>, includeDiagonals: Boolean = false): List<Point2D<Int>> {
        val list = current.getNeighbors(includeDiagonals)
        list.removeIf { pair -> !isInBounds(pair.x, pair.y) }
        return list
    }

    /**
     * Return true if the point is in bounds.
     */
    fun isInBounds(x: Int, y: Int): Boolean {
        return (x in xRange) && (y in yRange)
    }

    fun isInBounds(point: Point2D<Int>): Boolean = isInBounds(point.x, point.y)

    /**
     * Gets a value in the grid.
     */
    operator fun get(x: Int, y: Int): T = grid[toIndex(x, y)]
    operator fun get(pair: Point2D<Int>): T = get(pair.x, pair.y)

    /**
     * Sets a value in the grid.
     */
    operator fun set(x: Int, y: Int, value: T) {
        grid[toIndex(x, y)] = value
    }

    operator fun set(point: Point2D<Int>, value: T) = set(point.x, point.y, value)

    /**
     * Converts a 2D coordinate into a 1D index.
     */
    private fun toIndex(x: Int, y: Int): Int {
        val index = y * width + x
        if (index !in grid.indices) {
            throw IndexOutOfBoundsException("($x,$y) is out of bounds ($xRange,$yRange).")
        }
        return index
    }

    /**
     * Adds space between numeric values in the grid output.
     */
    override fun toString(): String {
        val output = StringBuilder()
        for (y in yRange) {
            for (x in xRange) {
                val value = get(x, y)
                output.append(get(x, y))
                if (value is Int) {
                    output.append("\t")
                }
            }
            output.append("\n")
        }
        return output.toString()
    }

    companion object {
        /**
         * Builds a grid from a number-based input.
         */
        fun fromIntInput(input: List<String>): Grid<Int> {
            val hasSpaces = input[0].contains(" ")
            val width = if (hasSpaces) input[0].extractInts().size else input[0].length
            val grid = Grid(width, input.size, 0)
            for ((y, line) in input.withIndex()) {
                if (hasSpaces) {
                    for ((x, value) in line.extractInts().withIndex()) {
                        grid[x, y] = value
                    }
                } else {
                    for ((x, value) in line.withIndex()) {
                        grid[x, y] = value.digitToInt()
                    }
                }
            }
            return grid
        }

        /**
         * Builds a grid from a character-based input.
         */
        fun fromCharInput(input: List<String>): Grid<Char> {
            val grid = Grid(input[0].length, input.size, '?')
            for ((y, line) in input.withIndex()) {
                for ((x, value) in line.withIndex()) {
                    grid[x, y] = value
                }
            }
            return grid
        }
    }
}

enum class Orientation { ORIGINAL, CLOCKWISE_90, CLOCKWISE_180, CLOCKWISE_270, MIRROR_H, MIRROR_V }