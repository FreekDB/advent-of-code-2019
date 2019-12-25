package com.github.freekdb.aoc2019

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

private const val AMPLIFIER_COUNT = 5

private var DEBUG_LOGGING = false

// Warning: the code in this file could be very painful to read!

// As I'm still new to Kotlin in general and coroutines & channels specifically, the code in this file can be vastly
// improved! I'll compare with others. For now, the ugly code below produced two correct answers and won me two stars,
// so I'll proudly put it in the repository anyway... ;-)

// When I started solving part 2, channels seemed to be a good way to allow the amplifiers to communicate. The solution
// for part 1 relied on a simpler way of communication, which is why I kept both the older IntcodeComputerDay7Part1
// class and the newer IntcodeComputer class.

// December 2019, Freek de Bruijn

// https://adventofcode.com/2019/day/7
fun main(arguments: Array<String>) {
    DEBUG_LOGGING = arguments.contains("debug-logging")

    val amplifierControllerSoftware = File("input/day-07--input.txt")
        .readLines()[0]
        .split(",")
        .map { it.toInt() }

    solvePart1f(amplifierControllerSoftware)

    solvePart2(amplifierControllerSoftware)
}

private fun solvePart1f(amplifierControllerSoftware: List<Int>) {
    val highestSignal =
        listOf(0, 1, 2, 3, 4)
            .permutations()
            .map { phaseSettings ->
                val output = mutableListOf(0)

                phaseSettings.forEach { inputSetting ->
                    val input = listOf(inputSetting, output.first())
                    output.clear()

                    val amplifier = IntcodeComputerDay7Part1(amplifierControllerSoftware, input, output)
                    amplifier.runProgram()
                }

                output[0]
            }
            .max()

    println("Highest signal for part 1: $highestSignal.")
}


// This is the older version of the IntcodeComputer that I used for day 7 part 1 (without channels).
class IntcodeComputerDay7Part1(initialState: List<Int>, private val input: List<Int>,
                               private val output: MutableList<Int>) {
    private val memory = initialState.toMutableList()
    private var inputIndex = 0

    fun runProgram() {
        var instructionPointer = 0
        var instructionHeader = memory[instructionPointer]
        var opcode = Opcode.byValue(instructionHeader % 100)

        while (opcode != Opcode.OPCODE_HALT) {
            when (opcode) {
                Opcode.OPCODE_PLUS -> plus(instructionHeader, instructionPointer)
                Opcode.OPCODE_TIMES -> times(instructionHeader, instructionPointer)
                Opcode.OPCODE_INPUT -> readValueFromInput(instructionPointer)
                Opcode.OPCODE_OUTPUT -> printValueToOutput(instructionHeader, instructionPointer)
                Opcode.OPCODE_JUMP_TRUE -> instructionPointer = jumpIfTrue(instructionHeader, instructionPointer)
                Opcode.OPCODE_JUMP_FALSE -> instructionPointer = jumpIfFalse(instructionHeader, instructionPointer)
                Opcode.OPCODE_LESS_THAN -> storeLessThan(instructionHeader, instructionPointer)
                Opcode.OPCODE_EQUALS -> storeEquals(instructionHeader, instructionPointer)
                Opcode.OPCODE_HALT -> Unit
            }

            if (opcode.instructionLength > 0) {
                instructionPointer += opcode.instructionLength
            }

            instructionHeader = memory[instructionPointer]
            opcode = Opcode.byValue(instructionHeader % 100)
        }
    }

    private fun plus(instructionHeader: Int, instructionPointer: Int) {
        val operand1 = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val operand2 = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        setValue(instructionPointer + 3, operand1 + operand2)
    }

    private fun times(instructionHeader: Int, instructionPointer: Int) {
        val operand1 = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val operand2 = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        setValue(instructionPointer + 3, operand1 * operand2)
    }

    private fun readValueFromInput(instructionPointer: Int) {
        val value = input[inputIndex]
        inputIndex++

        setValue(instructionPointer + 1, value)
        // println("Input value: $value")
    }

    private fun printValueToOutput(instructionHeader: Int, instructionPointer: Int) {
        val value = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)

        output.add(value)
        // println("Output value: $value")
    }

    private fun jumpIfTrue(instructionHeader: Int, instructionPointer: Int): Int {
        val value = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val destination = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        return if (value != 0) destination else instructionPointer + 3
    }

    private fun jumpIfFalse(instructionHeader: Int, instructionPointer: Int): Int {
        val value = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val destination = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        return if (value == 0) destination else instructionPointer + 3
    }

    private fun storeLessThan(instructionHeader: Int, instructionPointer: Int) {
        val operand1 = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val operand2 = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        setValue(instructionPointer + 3, if (operand1 < operand2) 1 else 0)
    }

    private fun storeEquals(instructionHeader: Int, instructionPointer: Int) {
        val operand1 = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val operand2 = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        setValue(instructionPointer + 3, if (operand1 == operand2) 1 else 0)
    }

    private fun getValue(memoryAddress: Int, parameterMode: Int): Int {
        val value = memory[memoryAddress]

        return if (parameterMode == PARAMETER_MODE_POSITION) memory[value] else value
    }

    private fun setValue(memoryAddress: Int, value: Int) {
        memory[memory[memoryAddress]] = value
    }
}


private fun solvePart2(amplifierControllerSoftware: List<Int>) {
    val highestSignal =
        (5 until 5 + AMPLIFIER_COUNT)
            .toList()
            .permutations()
            .map { phaseSettings ->
                val finalOutput = determineFinalOutput(phaseSettings, amplifierControllerSoftware)

                if (DEBUG_LOGGING)
                    println("[${System.nanoTime()}] Final output: $finalOutput.")

                finalOutput
            }.max()

    println("Highest signal for part 2: $highestSignal.")
}

private fun determineFinalOutput(phaseSettings: List<Int>, amplifierControllerSoftware: List<Int>): Int {
    var finalOutput: Int = Int.MIN_VALUE

    val channels = (1..AMPLIFIER_COUNT).map { Channel<Int>() }
    val finalOutputChannel = Channel<Int>()

    val logChannel = Channel<String>()

    val amplifiers = (1..AMPLIFIER_COUNT).map { amplifierIndex ->
        val inputChannel = channels[amplifierIndex - 1]
        val outputChannel = if (amplifierIndex < AMPLIFIER_COUNT) channels[amplifierIndex] else channels[0]

        IntcodeComputer("a$amplifierIndex", amplifierControllerSoftware, inputChannel, outputChannel, logChannel,
            if (amplifierIndex < AMPLIFIER_COUNT) null else finalOutputChannel)
    }

    runBlocking {
        amplifiers.forEach {
            launch {
                it.runProgram(if (it.name == "a1") amplifiers[AMPLIFIER_COUNT - 1] else null)
            }
        }

        launch {
            phaseSettings.forEachIndexed { settingIndex, phaseSetting ->
                channels[settingIndex].send(phaseSetting)
            }

            channels[0].send(0)

            if (DEBUG_LOGGING) {
                println(">>>>>> Settings and zero have been sent to input channels.")
                println()
            }
        }

        launch {
            var previousLine = ""

            for (line in logChannel) {
                if (DEBUG_LOGGING && (line.isNotEmpty() || previousLine.isNotEmpty())) {
                    // Add an empty line as a separator if the two messages are from different amplifiers.
                    if (previousLine.length >= 4 && line.length >= 4 && previousLine.take(4) != line.take(4))
                        println("")

                    println(line)
                }

                previousLine = line
            }

            if (DEBUG_LOGGING)
                println(">>>>>> Log channel printer has stopped.")
        }

        finalOutput = finalOutputChannel.receive()

        coroutineContext.cancelChildren()
    }

    return finalOutput
}

// Thanks to Jesse for this elegant permutations function!
fun <T> List<T>.permutations(): List<List<T>> {
    return if (this.size <= 1)
        listOf(this)
    else
        this.flatMap { item ->
            this.minus(item).permutations().map { subPermutation -> subPermutation + item }
        }
}


enum class Opcode(val value: Int, val instructionLength: Int = 0) {
    OPCODE_PLUS(1, 4),
    OPCODE_TIMES(2, 4),
    OPCODE_INPUT(3, 2),
    OPCODE_OUTPUT(4, 2),
    OPCODE_JUMP_TRUE(5),
    OPCODE_JUMP_FALSE(6),
    OPCODE_LESS_THAN(7, 4),
    OPCODE_EQUALS(8, 4),
    OPCODE_HALT(99);

    companion object {
        fun byValue(opcodeValue: Int): Opcode =
            values().firstOrNull { it.value == opcodeValue } ?: OPCODE_HALT
    }
}


private const val PARAMETER_MODE_POSITION = 0
// private const val PARAMETER_MODE_IMMEDIATE = 1


class IntcodeComputer(val name: String, initialState: List<Int>, private val inputChannel: Channel<Int>,
                      private val outputChannel: Channel<Int>, private val logChannel: Channel<String>,
                      private val finalOutputChannel: Channel<Int>? = null) {
    private val memory = initialState.toMutableList()
    private var useFinalOutput = false

    suspend fun runProgram(lastAmplifier: IntcodeComputer? = null) {
        log("Starting program for amplifier $name.")

        var instructionPointer = 0
        var instructionHeader = memory[instructionPointer]
        var opcode = Opcode.byValue(instructionHeader % 100)

        while (opcode != Opcode.OPCODE_HALT) {
            log()
            log("Executing opcode ${opcode.name.toLowerCase()} (instruction pointer: $instructionPointer).")

            when (opcode) {
                Opcode.OPCODE_PLUS -> plus(instructionHeader, instructionPointer)
                Opcode.OPCODE_TIMES -> times(instructionHeader, instructionPointer)
                Opcode.OPCODE_INPUT -> readValueFromInput(instructionPointer)
                Opcode.OPCODE_OUTPUT -> writeValueToOutput(instructionHeader, instructionPointer)
                Opcode.OPCODE_JUMP_TRUE -> instructionPointer = jumpIfTrue(instructionHeader, instructionPointer)
                Opcode.OPCODE_JUMP_FALSE -> instructionPointer = jumpIfFalse(instructionHeader, instructionPointer)
                Opcode.OPCODE_LESS_THAN -> storeLessThan(instructionHeader, instructionPointer)
                Opcode.OPCODE_EQUALS -> storeEquals(instructionHeader, instructionPointer)
                Opcode.OPCODE_HALT -> Unit
            }

            if (opcode.instructionLength > 0) {
                instructionPointer += opcode.instructionLength
            }

            instructionHeader = memory[instructionPointer]
            opcode = Opcode.byValue(instructionHeader % 100)
        }

        if (name == "a1" && lastAmplifier != null) {
            log("Notifying a5 to send its final output to a different channel.")
            lastAmplifier.switchToFinalOutput()
        }

        log("Closing input channel.")
        inputChannel.close()

        log("Program is halting.")
    }

    private suspend fun switchToFinalOutput() {
        log("Switching to final output channel.")
        useFinalOutput = true
    }

    private suspend fun plus(instructionHeader: Int, instructionPointer: Int) {
        val operand1 = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val operand2 = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        setValue(instructionPointer + 3, operand1 + operand2)
    }

    private suspend fun times(instructionHeader: Int, instructionPointer: Int) {
        val operand1 = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val operand2 = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        setValue(instructionPointer + 3, operand1 * operand2)
    }

    private suspend fun readValueFromInput(instructionPointer: Int) {
        val value = inputChannel.receive()

        log("Read value $value from input channel.")
        setValue(instructionPointer + 1, value)
    }

    private suspend fun writeValueToOutput(instructionHeader: Int, instructionPointer: Int) {
        val value = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)

        if (!useFinalOutput)
            outputChannel.send(value)
        else
            finalOutputChannel?.send(value)

        log("Wrote value $value to ${if (useFinalOutput) "final" else ""} output channel.")
    }

    private suspend fun jumpIfTrue(instructionHeader: Int, instructionPointer: Int): Int {
        val value = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val destination = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        if (value != 0)
            log("Changed instruction pointer to $destination.")

        return if (value != 0) destination else instructionPointer + 3
    }

    private suspend fun jumpIfFalse(instructionHeader: Int, instructionPointer: Int): Int {
        val value = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val destination = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        if (value == 0)
            log("Changed instruction pointer to $destination.")

        return if (value == 0) destination else instructionPointer + 3
    }

    private suspend fun storeLessThan(instructionHeader: Int, instructionPointer: Int) {
        val operand1 = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val operand2 = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        setValue(instructionPointer + 3, if (operand1 < operand2) 1 else 0)
    }

    private suspend fun storeEquals(instructionHeader: Int, instructionPointer: Int) {
        val operand1 = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)
        val operand2 = getValue(instructionPointer + 2, (instructionHeader / 1000) % 10)

        setValue(instructionPointer + 3, if (operand1 == operand2) 1 else 0)
    }

    private suspend fun getValue(memoryAddress: Int, parameterMode: Int): Int {
        val actualAddress = if (parameterMode == PARAMETER_MODE_POSITION) memory[memoryAddress] else memoryAddress
        val actualValue = memory[actualAddress]

        log("Read value $actualValue from memory location $actualAddress.")

        return actualValue
    }

    private suspend fun setValue(memoryAddress: Int, value: Int) {
        memory[memory[memoryAddress]] = value

        log("Wrote value $value to memory location ${memory[memoryAddress]}.")
    }

    private suspend fun log(line: String = "") {
        logChannel.send(if (line.isEmpty()) "" else "[$name ${System.nanoTime()}] $line")
    }
}
