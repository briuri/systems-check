package buri.sc.common.position

import buri.sc.common.position.Direction.*

/**
 * Helper class for a 2D point and directional facing.
 *
 * @author Brian Uri!
 */
data class MutablePosition(var coords: Point2D<Long>, var facing: Direction = NORTH) {

    constructor(x: Long, y: Long, facing: Direction) : this(Point2D(x, y), facing)

    /**
     * Adjusts facing and moves in that direction.
     */
    fun move(direction: Direction) {
        facing = direction
        move()
    }

    /**
     * Move one square in the current direction.
     */
    fun move() {
        move(1L)
    }

    /**
     * Move multiple squares.
     */
    fun move(length: Long) {
        coords = when (facing) {
            NORTH -> coords.copy(y = coords.y - length)
            EAST -> coords.copy(x = coords.x + length)
            SOUTH -> coords.copy(y = coords.y + length)
            WEST -> coords.copy(x = coords.x - length)
        }
    }

    /**
     * Change the facing on this position
     */
    fun turnLeft() {
        facing = when (facing) {
            NORTH -> WEST
            EAST -> NORTH
            SOUTH -> EAST
            WEST -> SOUTH
        }
    }

    /**
     * Change the facing on this position
     */
    fun turnRight() {
        facing = when (facing) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }
    }

    /**
     * Change the facing on this position
     */
    fun turnAround() {
        facing = when (facing) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
    }
}

enum class Direction(val icon: Char) {
    NORTH('^'), EAST('>'), SOUTH('v'), WEST('<');

    /**
     * Returns a direction matching the given icon.
     */
    companion object {
        infix fun from(value: Char): Direction {
            return entries.first { it.icon == value }
        }
    }
}