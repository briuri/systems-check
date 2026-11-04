package buri.sc.y26.q10

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import buri.sc.common.extractInts
import buri.sc.common.position.Point2D
import buri.sc.y26.q10.Puzzle.Companion.STAGING
import buri.sc.y26.q10.Puzzle.Companion.UNLOCKED
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(191, 1)
        assertRun(230204, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(142, 1)
        assertRun(15378, 0, true)

    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        // Initialize workshop components.
        val jobIds = input.filter { it.startsWith("J") }.map { it.drop(1).toInt() }
        val toolIds = input.filter { it.startsWith("OP") }.map { it.split(" ")[2].drop(1).toInt() }.toSet()
        val parts = mutableMapOf<Int, JobPart>()
        jobIds.forEach { parts[it] = JobPart(it) }
        val machines = mutableMapOf<Int, Machine>()
        (1..24).forEach { machines[it] = Machine(it) }
        val tools = mutableMapOf<Int, Tool>()
        toolIds.forEach { tools[it] = Tool(it) }

        // Initialize jobs.
        val jobs = mutableMapOf<Int, Job>()
        var i = 0
        while (i in input.indices) {
            val line = input[i]
            if (line.startsWith("J")) {
                val jobId = line.drop(1).toInt()
                val operations = mutableListOf<Operation>()
                do {
                    i++
                    val numbers = input[i].extractInts()
                    operations.add(
                        Operation(
                            numbers[0],
                            parts[jobId]!!,
                            machines[numbers[1]]!!,
                            tools[numbers[2]]!!,
                            numbers[3]
                        )
                    )
                } while ((i + 1) in input.indices && input[i + 1] != "")
                jobs[jobId] = Job(jobId, operations)
                i += 2
            }
        }
        if (part.isOne()) {
            return getSerialTime(parts.values.toList(), tools.values.toList(), jobs.values.toList())
        }
        // Part 2
        return getParallelTime(parts.values.toList(), tools.values.toList(), jobs.values.toList())
    }

    fun getSerialTime(parts: List<JobPart>, tools: List<Tool>, jobs: List<Job>): Int {
        var time = 0
        while (!(jobs.all { it.isDone() })) {
            val currentJob = jobs.firstOrNull { !it.isDone() }
            if (currentJob != null) {
                parts.forEach { it.move() }
                tools.forEach { it.move() }

                val runningOp = currentJob.operations.firstOrNull { it.status == Status.RUNNING }
                if (runningOp != null && runningOp.endTime == time) {
                    runningOp.status = Status.DONE
                    runningOp.tool.enrouteTo = STAGING
                    if (runningOp == currentJob.operations.last()) {
                        runningOp.part.enrouteTo = STAGING
                        continue
                    }
                }
                if (currentJob.operations.none { it.status == Status.RUNNING }) {
                    val waitingOp = currentJob.operations.firstOrNull { it.status == Status.WAITING }
                    if (waitingOp != null) {
                        val partReady = waitingOp.part.position == waitingOp.machine.position
                        val toolReady = waitingOp.tool.position == waitingOp.machine.position
                        if (!partReady && waitingOp.part.enrouteTo == null) {
                            waitingOp.part.enrouteTo = waitingOp.machine.position
                        }
                        if (!toolReady && waitingOp.tool.enrouteTo == null) {
                            waitingOp.tool.enrouteTo = waitingOp.machine.position
                        }
                        if (partReady && toolReady) {
                            waitingOp.status = Status.RUNNING
                            waitingOp.endTime = time + waitingOp.duration
                        }
                    }
                }
            }
            time++
        }
        return time
    }
    
    fun getParallelTime(parts: List<JobPart>, tools: List<Tool>, jobs: List<Job>): Int {
        var time = 0
        while (!(jobs.all { it.isDone() })) {
            val finishedOps = mutableListOf<Operation>()
            parts.forEach { it.move() }
            tools.forEach { it.move() }
            for ((_, operations) in jobs.filter { !it.isDone()}) {
                val runningOp = operations.firstOrNull { it.status == Status.RUNNING }
                if (runningOp != null && runningOp.endTime == time) {
                    runningOp.status = Status.DONE
                    runningOp.machine.lockedBy = UNLOCKED
                    runningOp.tool.enrouteTo = STAGING
                    finishedOps.add(runningOp)
                    if (runningOp == operations.last()) {
                        runningOp.part.enrouteTo = STAGING
                        continue
                    }
                }
            }
            for ((jobId, operations) in jobs.filter { !it.isDone()}) {
                if (operations.none { it.status == Status.RUNNING }) {
                    val waitingOp = operations.firstOrNull { it.status == Status.WAITING }
                    if (waitingOp != null && waitingOp.machine.canUse(jobId) &&  waitingOp.part.canUse(jobId) &&  waitingOp.tool.canUse(jobId)) {
                        waitingOp.machine.lockedBy = jobId
                        waitingOp.part.lockedBy = jobId
                        finishedOps.removeIf { it.part == waitingOp.part }
                        waitingOp.tool.lockedBy = jobId
                        val partReady = waitingOp.part.canUse(jobId) && waitingOp.part.position == waitingOp.machine.position
                        val toolReady = waitingOp.tool.canUse(jobId) && waitingOp.tool.position == waitingOp.machine.position
                        if (!partReady && waitingOp.part.enrouteTo == null) {
                            waitingOp.part.enrouteTo = waitingOp.machine.position
                        }
                        if (!toolReady && waitingOp.tool.enrouteTo == null) {
                            waitingOp.tool.enrouteTo = waitingOp.machine.position
                        }
                        if (partReady && toolReady) {
                            waitingOp.status = Status.RUNNING
                            waitingOp.endTime = time + waitingOp.duration
                        }
                    }
                }
            }
            for ((_, part) in finishedOps) {
                part.enrouteTo = STAGING
            }
            time++
        }
        return time - 1
    }
    
    companion object {
        val STAGING = Point2D(0, 0)
        const val UNLOCKED = -1
    }
}
abstract class MoveableItem {
    var lockedBy = UNLOCKED
    var position = STAGING
    var enrouteTo: Point2D<Int>? = null

    fun canUse(jobId: Int): Boolean = lockedBy in listOf(UNLOCKED, jobId)
    
    fun move() {
        if (enrouteTo != null) {
            if (position.x < enrouteTo!!.x) {
                position = Point2D(position.x + 1, position.y)
            }
            else if (position.x > enrouteTo!!.x) {
                position = Point2D(position.x - 1, position.y)
            }
            else if (position.y < enrouteTo!!.y) {
                position = Point2D(position.x, position.y + 1)
            }
            else if (position.y > enrouteTo!!.y) {
                position = Point2D(position.x, position.y - 1)
            }
            if (position == enrouteTo) {
                if (enrouteTo == STAGING) {
                    lockedBy = -1
                }
                enrouteTo = null                
            }
        }
    }
}
data class JobPart(val id: Int): MoveableItem() {
    override fun toString(): String = "P$id($position)"
}
data class Machine(val id: Int) {
    var lockedBy = UNLOCKED
    val position = Point2D(id % 5, id / 5)
    override fun toString(): String = "M$id($position)"
    
    fun canUse(jobId: Int): Boolean = lockedBy in listOf(UNLOCKED, jobId)
}

data class Tool(val id: Int): MoveableItem() {
    override fun toString(): String = "T$id($position, lockedBy=$lockedBy)"
}

data class Job(val id: Int, val operations: List<Operation>) {
    fun isDone() = operations.all { it.status == Status.DONE && it.part.position == STAGING }
}
data class Operation(val id: Int, val part: JobPart, val machine: Machine, val tool: Tool, val duration: Int) {
    var status = Status.WAITING
    var endTime = -1
    override fun toString(): String = "J${part.id}(OP$id M${machine.id} T${tool.id} D${duration})"
}
enum class Status {
    WAITING, RUNNING, DONE
}