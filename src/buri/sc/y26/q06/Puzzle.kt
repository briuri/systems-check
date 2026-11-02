package buri.sc.y26.q06

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
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
        assertRun(6, 1)
        assertRun(5943, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(0, 1)
        assertRun(155, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val asteroids = mutableSetOf<Body>()
        for (line in input) {
            val tokens = line.split(" ")
            asteroids.add(Body(Point2D.fromIntInput(tokens[0]), Point2D.fromIntInput(tokens[1])))
        }
        val ships = mutableSetOf<Body>()
        if (part.isOne()) {
            ships.add(Body(Point2D(0, 0), Point2D(0, 1)))
        } else {
            for (burn in -10..10) {
                ships.add(Body(Point2D(burn, 0), Point2D(0, 1)))
            }
        }

        val collisions = mutableMapOf<Body, Int>()
        for (ship in ships) {
            var hits = 0
            for (time in 0..10_000) {
                hits += asteroids.count { it.getPositionAt(time) == ship.getPositionAt(time) }
            }
            collisions[ship] = hits
        }
        return collisions.minOf { it.value }
    }
}

data class Body(val start: Point2D<Int>, val velocity: Point2D<Int>) {
    fun getPositionAt(time: Int): Point2D<Int> {
        return Point2D(start.x + velocity.x * time, start.y + velocity.y * time)
    }
}