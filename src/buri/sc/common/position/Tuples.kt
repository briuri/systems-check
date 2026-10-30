package buri.sc.common.position

import kotlin.math.absoluteValue

/**
 * 2D, 3D, and 4D points using Ints and Longs
 *
 * @author Brian Uri!
 */

/**
 * Base Class
 */
abstract class BaseTuple<T> {

    /**
     * Enforce type restrictions (only Ints and Longs).
     */
    protected fun <T> assertValidType(a: T) {
        if (a !is Int && a !is Long) {
            throw IllegalArgumentException("Only Int and Longs can be stored in tuples.")
        }
    }

    /**
     * Does a comparison of two generics that are Ints or Longs.
     */
    protected fun <T> compareNumber(a: T, b: T): Int {
        return if (a is Int && b is Int) {
            a.compareTo(b)
        } else if (a is Long && b is Long) {
            a.compareTo(b)
        } else {
            0
        }
    }

    /**
     * Gets absolute value of difference of two generics that are Ints or Longs.
     */
    protected fun <T> abs(a: T, b: T): Long {
        return if (a is Int && b is Int) {
            (a - b).absoluteValue.toLong()
        } else if (a is Long && b is Long) {
            (a - b).absoluteValue
        } else {
            0L
        }
    }
}

/**
 * 2D Points
 */
data class Point2D<T>(val x: T, val y: T) : BaseTuple<T>(), Comparable<Point2D<T>> {

    init {
        assertValidType(x)
    }

    /**
     * Returns the Manhattan distance to another point.
     */
    fun getManhattanDistance(that: Point2D<T>): Long {
        return (abs(x, that.x) + abs(y, that.y))
    }

    /**
     * Compares using reading order (Top to Bottom, Left to Right)
     */
    override fun compareTo(other: Point2D<T>): Int {
        var compare = compareNumber(y, other.y)
        if (compare == 0) {
            compare = compareNumber(x, other.x)
        }
        return compare
    }

    override fun toString(): String {
        return "$x,$y"
    }

    companion object {
        /**
         * Builds a point from a string of comma-separated Ints.
         */
        fun fromIntInput(data: String): Point2D<Int> {
            val numbers = data.split(",").map { it.toInt() }
            return Point2D(numbers[0], numbers[1])
        }
    }
}

/**
 * The rectangular bounds for a set of Int points.
 *
 * @author Brian Uri!
 */
class Bounds2D(points: Set<Point2D<Int>>) {
    val x: IntRange
    val y: IntRange
    val area: Long
        get() = (x.last - x.first).toLong() * (y.last - y.first).toLong()

    init {
        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }
        x = minX..maxX
        y = minY..maxY
    }

    override fun toString(): String = "x=$x,y=$y"
}

/**
 * Gets neighbors of an Int point in reading order. Can be toggled for diagonally adjacent spots.
 */
fun Point2D<Int>.getNeighbors(includeDiagonals: Boolean = false): MutableList<Point2D<Int>> {
    val neighbors = mutableListOf<Point2D<Int>>()
    if (includeDiagonals) {
        neighbors.add(Point2D(x - 1, y - 1))
    }
    neighbors.add(Point2D(x, y - 1))
    if (includeDiagonals) {
        neighbors.add(Point2D(x + 1, y - 1))
    }
    neighbors.add(Point2D(x - 1, y))
    neighbors.add(Point2D(x + 1, y))
    if (includeDiagonals) {
        neighbors.add(Point2D(x - 1, y + 1))
    }
    neighbors.add(Point2D(x, y + 1))
    if (includeDiagonals) {
        neighbors.add(Point2D(x + 1, y + 1))
    }
    return neighbors
}

/**
 * 3D Points
 */
data class Point3D<T>(val x: T, val y: T, val z: T) : BaseTuple<T>(), Comparable<Point3D<T>> {

    init {
        assertValidType(x)
    }

    /**
     * Returns the Manhattan distance to another point.
     */
    fun getManhattanDistance(that: Point3D<T>): Long {
        return (abs(x, that.x) + abs(y, that.y) + abs(z, that.z))
    }

    /**
     * Compares using reading order (Top to Bottom, Left to Right)
     */
    override fun compareTo(other: Point3D<T>): Int {
        var compare = compareNumber(z, other.z)
        if (compare == 0) {
            compare = compareNumber(y, other.y)
        }
        if (compare == 0) {
            compare = compareNumber(x, other.x)
        }
        return compare
    }

    override fun toString(): String {
        return "$x,$y,$z"
    }
}

/**
 * The rectangular bounds for a set of Int points.
 *
 * @author Brian Uri!
 */
class Bounds3D(points: Set<Point3D<Int>>) {
    val x = (points.minOf { it.x })..(points.maxOf { it.x })
    val y = (points.minOf { it.y })..(points.maxOf { it.y })
    val z = (points.minOf { it.z })..(points.maxOf { it.z })

    override fun toString(): String = "x=$x,y=$y,z=$z"
}

/**
 * Gets all neighbors of an Int point (9 above, 8 around, and 9 below).
 */
fun Point3D<Int>.getNeighbors(): MutableList<Point3D<Int>> {
    val neighbors = mutableListOf<Point3D<Int>>()
    for (dz in -1..1) {
        for (dy in -1..1) {
            for (dx in -1..1) {
                val point = Point3D(x + dx, y + dy, z + dz)
                if (point != this) {
                    neighbors.add(point)
                }
            }
        }
    }
    return neighbors
}

/**
 * 4D Points
 */
data class Point4D<T>(val x: T, val y: T, val z: T, val t: T) : BaseTuple<T>(), Comparable<Point4D<T>> {

    init {
        assertValidType(x)
    }

    /**
     * Returns the Manhattan distance to another point.
     */
    fun getManhattanDistance(that: Point4D<T>): Long {
        return (abs(x, that.x) + abs(y, that.y) + abs(z, that.z) + abs(t, that.t))
    }

    /**
     * Compares using reading order (Top to Bottom, Left to Right)
     */
    override fun compareTo(other: Point4D<T>): Int {
        var compare = compareNumber(t, other.t)
        if (compare == 0) {
            compare = compareNumber(z, other.z)
        }
        if (compare == 0) {
            compare = compareNumber(y, other.y)
        }
        if (compare == 0) {
            compare = compareNumber(x, other.x)
        }
        return compare
    }

    override fun toString(): String {
        return "$x,$y,$z,$t"
    }
}

/**
 * The rectangular bounds for a set of Int points.
 *
 * @author Brian Uri!
 */
class Bounds4D(points: Set<Point4D<Int>>) {
    val x = (points.minOf { it.x })..(points.maxOf { it.x })
    val y = (points.minOf { it.y })..(points.maxOf { it.y })
    val z = (points.minOf { it.z })..(points.maxOf { it.z })
    val t = (points.minOf { it.t })..(points.maxOf { it.t })

    override fun toString(): String = "x=$x,y=$y,z=$z,t=$t"
}

/**
 * Gets all neighbors of an Int point (80 total).
 */
fun Point4D<Int>.getNeighbors(): MutableList<Point4D<Int>> {
    val neighbors = mutableListOf<Point4D<Int>>()
    for (dt in -1..1) {
        for (dz in -1..1) {
            for (dy in -1..1) {
                for (dx in -1..1) {
                    val point = Point4D(x + dx, y + dy, z + dz, t + dt)
                    if (point != this) {
                        neighbors.add(point)
                    }
                }
            }
        }
    }
    return neighbors
}