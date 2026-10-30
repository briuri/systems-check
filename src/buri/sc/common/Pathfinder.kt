package buri.sc.common

/**
 * Search utility for finding paths between points.
 *
 * T is a Point2D<Int> or a Point3D<Int>.
 *
 * @author Brian Uri!
 */
class Pathfinder<T>(val stepStrategy: (T) -> List<T>) {

    /**
     * Returns a "came from" map showing all the reachable spaces from a particular space. The entry for the
     * starting position start will have a null value.
     */
    fun exploreFrom(start: T): Map<T, T?> {
        val frontier = ArrayDeque<T>()
        frontier.add(start)

        val cameFrom = mutableMapOf<T, T?>()
        cameFrom[start] = null

        var current: T?
        while (frontier.isNotEmpty()) {
            current = frontier.removeFirst()
            for (next in stepStrategy(current).filter { !cameFrom.containsKey(it) }) {
                frontier.add(next)
                cameFrom[next] = current
            }
        }
        return cameFrom
    }
}

/**
 * Extension function to count the steps in a "came from" map. Returns -1 if there is no path between the points.
 */
fun <T> Map<T, T?>.countSteps(start: T, end: T): Int {
    if (start == end) {
        return 0
    }
    var steps = 0
    var current: T? = end
    while (current != null) {
        steps++
        current = this[current]
        if (current == start) {
            return steps
        }
    }
    return -1
}