package buri.sc.y26.q10

import buri.sc.common.BasePuzzle
import buri.sc.common.Part
import buri.sc.common.extractInts
import buri.sc.common.position.Point2D
import org.junit.Test
import kotlin.math.max

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
        // Incorrect: 15499, 15852, 16020
        assertRun(0, 0, true)
        
        // Extra Input from secondary account
        // Incorrect: 15193
//        assertRun(0, 2, true)
    }

    val staging = Point2D(0, 0)
    
    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val machines = mutableMapOf<Int, Machine>()
        for (i in 1..24) {
            machines[i] = Machine(i)
        }
        
        val jobs = mutableListOf<List<Operation>>()
        var i = 0
        while (i in input.indices) {
            val line = input[i]
            val jobOps = mutableListOf<Operation>()
            if (line.startsWith("J")) {
                i++
                while (i in input.indices && input[i] != "") {
                    jobOps.add(Operation(input[i]))
                    i++
                }
                i++
            }
            jobOps[0].prevPartPosition = staging
            for (j in 1..<jobOps.size) {
                jobOps[j].prevPartPosition = machines[jobOps[j - 1].machine]!!.position()
            }
            jobs.add(jobOps)            
        }        
               
        if (part.isOne()) {
            return jobs.sumOf { getSerialTime(machines, it) }
        } 
        // Part 2
        return getParallelTime(machines, jobs)
    }
    
    fun getSerialTime(machines: Map<Int, Machine>, job: List<Operation>): Long {
        var time = 0L
        var partPos = staging
        for (op in job) {
            val pos = machines[op.machine]!!.position()
            val partTime = pos.getManhattanDistance(partPos)
            val toolTime = pos.getManhattanDistance(staging)
            time += max(partTime, toolTime)
            time += op.duration
            partPos = pos
        }
        time += staging.getManhattanDistance(partPos)
        return time
    }
    
    fun getParallelTime(machines: Map<Int, Machine>, jobs: List<List<Operation>>): Int {
        val lockedMachines = mutableSetOf<Int>()
        val lockedTools = mutableSetOf<Int>()
        val returningTools = mutableMapOf<Int, Int>()
        val freedParts = mutableMapOf<Int, Operation>()
        val returningParts = mutableMapOf<Int, Int>()
        
        var time = 0
        while (!(jobs.flatten().all { it.status == Status.DONE } && returningParts.isEmpty() && returningTools.isEmpty() )) {
            println("t=$time")
            
            // Check if parts have returned to staging.
            returningParts.filter { it.value == time }.forEach {
                println("\tRETURNED Part ${it.key + 1} at staging.")
                returningParts.remove(it.key)
            }
            
            // Check if tools have returned to staging.
            returningTools.filter { it.value == time }.forEach {
                println("\tRETURNED Tool ${it.key} at staging.")
                returningTools.remove(it.key) 
            }
            
            // Check running operations and end finished ones.
            for ((index, job) in jobs.withIndex()) {
                val op = job.firstOrNull { it.status == Status.RUNNING }
                if (op != null && op.endTime == time) {
                    op.status = Status.DONE
                    lockedMachines.remove(op.machine)
                    lockedTools.remove(op.tool)
                    val machinePosition = machines[op.machine]!!.position()
                    returningTools[op.tool] = time + machinePosition.getManhattanDistance(staging).toInt()
                    freedParts[index] = op
                    println("\tSTOP     J${index + 1}$op")
                    println("\tRELEASE  Part ${index + 1} freed on M${op.machine}.")
                    println("\tSENDBACK Tool ${op.tool} arriveTime=${returningTools[op.tool]!!}.")
                }                
            }
            
            // Check jobs that are idle and start any operation that can start.
            for ((index, job) in jobs.withIndex()) {
                val op = job.firstOrNull { it.status == Status.IDLE }
                if (op == null || job.any { it.status == Status.RUNNING }) {
                    continue
                }
                val isPartAvailable = index !in returningParts.keys
                val isToolAvailable = op.tool !in lockedTools && op.tool !in returningTools.keys
                if (op.machine !in lockedMachines && isPartAvailable && isToolAvailable) {
                    op.status = Status.RUNNING
                    lockedMachines.add(op.machine)
                    lockedTools.add(op.tool)
                    val machinePosition = machines[op.machine]!!.position()
                    val toolPosition = staging
                    if (index in freedParts.keys) {
                        println("\tCLAIM    Part ${index + 1} from M${freedParts[index]!!.machine}.")
                        freedParts.remove(index)
                    }
                    val partTime = machinePosition.getManhattanDistance(op.prevPartPosition)
                    val toolTime = machinePosition.getManhattanDistance(toolPosition)
                    op.endTime = time + max(partTime, toolTime).toInt() + op.duration
                    println("\tSTART    J${index + 1}$op endTime=${op.endTime}.")
                }
            }
            
            // Send recently released parts back to staging.
            for ((part, op) in freedParts) {
                returningParts[part] = time + staging.getManhattanDistance(machines[op.machine]!!.position()).toInt()
                op.prevPartPosition = staging
                println("\tSENDBACK Part ${part + 1} arriveTime=${returningParts[part]}.")
            }
            freedParts.clear()
            time++
        }
        return time - 1
    }
}
data class Operation(val line: String) {
    val numbers = line.extractInts()
    val machine = numbers[1]
    val tool = numbers[2]
    val duration = numbers[3]
    
    var prevPartPosition = Point2D(0, 0)
    var endTime = -1
    var status = Status.IDLE

    override fun toString(): String = "($line)"
}
enum class Status {
    IDLE, RUNNING, DONE
}
data class Machine(val id: Int) {
    fun position(): Point2D<Int> {
        val x = id % 5
        val y = id / 5
        return Point2D(x, y)
    }
}