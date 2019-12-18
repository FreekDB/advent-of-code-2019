package com.github.freekdb.aoc2019

import java.io.File

private const val OPCODE_PLUS = 1
private const val OPCODE_TIMES = 2
private const val OPCODE_INPUT = 3
private const val OPCODE_OUTPUT = 4
private const val OPCODE_JUMP_TRUE = 5
private const val OPCODE_JUMP_FALSE = 6
private const val OPCODE_LESS_THAN = 7
private const val OPCODE_EQUALS = 8
private const val OPCODE_HALT = 99

private const val PARAMETER_MODE_POSITION = 0
private const val PARAMETER_MODE_IMMEDIATE = 1

// https://adventofcode.com/2019/day/5
fun main() {
    val diagnosticProgram = File("input/day-05--input.txt")
        .readLines()
        .filter { it.isNotEmpty() }
        .flatMap { it.split(",") }
        .map { it.toInt() }

    runProgram(diagnosticProgram)
}

private fun runProgram(initialState: List<Int>): List<Int> {
    val memory = initialState.toMutableList()

    var instructionPointer = 0
    var instructionHeader = memory[instructionPointer]
    var opcode = instructionHeader % 100

    while (opcode != OPCODE_HALT) {
        if (opcode == OPCODE_PLUS) {
            plus(instructionHeader, instructionPointer, memory)
            instructionPointer += 4
        } else if (opcode == OPCODE_TIMES) {
            times(instructionHeader, instructionPointer, memory)
            instructionPointer += 4
        } else if (opcode == OPCODE_INPUT) {
            readValueFromInput(instructionPointer, memory)
            instructionPointer += 2
        } else if (opcode == OPCODE_OUTPUT) {
            printValueToOutput(instructionHeader, instructionPointer, memory)
            instructionPointer += 2
        } else if (opcode == OPCODE_JUMP_TRUE) {
            instructionPointer = jumpIfTrue(instructionHeader, instructionPointer, memory)
        } else if (opcode == OPCODE_JUMP_FALSE) {
            instructionPointer = jumpIfFalse(instructionHeader, instructionPointer, memory)
        } else if (opcode == OPCODE_LESS_THAN) {
            storeLessThan(instructionHeader, instructionPointer, memory)
            instructionPointer += 4
        } else if (opcode == OPCODE_EQUALS) {
            storeEquals(instructionHeader, instructionPointer, memory)
            instructionPointer += 4
        }

        instructionHeader = memory[instructionPointer]
        opcode = instructionHeader % 100
    }

    if (opcode == OPCODE_HALT) {
        println("Program is halting.")
    }

    return memory
}

private fun plus(instructionHeader: Int, instructionPointer: Int, memory: MutableList<Int>) {
    val operand1 = getValue(memory, instructionPointer + 1, (instructionHeader / 100) % 10)
    val operand2 = getValue(memory, instructionPointer + 2, (instructionHeader / 1000) % 10)

    setValue(memory, instructionPointer + 3, operand1 + operand2)
}

private fun times(instructionHeader: Int, instructionPointer: Int, memory: MutableList<Int>) {
    val operand1 = getValue(memory, instructionPointer + 1, (instructionHeader / 100) % 10)
    val operand2 = getValue(memory, instructionPointer + 2, (instructionHeader / 1000) % 10)

    setValue(memory, instructionPointer + 3, operand1 * operand2)
}

private fun readValueFromInput(instructionPointer: Int, memory: MutableList<Int>) {
    print("Please enter input value: ")
    val value = readLine()?.toInt() ?: 0

    setValue(memory, instructionPointer + 1, value)
}

private fun printValueToOutput(instructionHeader: Int, instructionPointer: Int, memory: MutableList<Int>) {
    val value = getValue(memory, instructionPointer + 1, (instructionHeader / 100) % 10)

    println("Output value: $value")
}

private fun jumpIfTrue(instructionHeader: Int, instructionPointer: Int, memory: MutableList<Int>): Int {
    val value = getValue(memory, instructionPointer + 1, (instructionHeader / 100) % 10)
    val destination = getValue(memory, instructionPointer + 2, (instructionHeader / 1000) % 10)

    return if (value != 0) destination else instructionPointer + 3
}

private fun jumpIfFalse(instructionHeader: Int, instructionPointer: Int, memory: MutableList<Int>): Int {
    val value = getValue(memory, instructionPointer + 1, (instructionHeader / 100) % 10)
    val destination = getValue(memory, instructionPointer + 2, (instructionHeader / 1000) % 10)

    return if (value == 0) destination else instructionPointer + 3
}

private fun storeLessThan(instructionHeader: Int, instructionPointer: Int, memory: MutableList<Int>) {
    val operand1 = getValue(memory, instructionPointer + 1, (instructionHeader / 100) % 10)
    val operand2 = getValue(memory, instructionPointer + 2, (instructionHeader / 1000) % 10)

    setValue(memory, instructionPointer + 3, if (operand1 < operand2) 1 else 0)
}

private fun storeEquals(instructionHeader: Int, instructionPointer: Int, memory: MutableList<Int>) {
    val operand1 = getValue(memory, instructionPointer + 1, (instructionHeader / 100) % 10)
    val operand2 = getValue(memory, instructionPointer + 2, (instructionHeader / 1000) % 10)

    setValue(memory, instructionPointer + 3, if (operand1 == operand2) 1 else 0)
}

private fun getValue(memory: MutableList<Int>, memoryAddress: Int, parameterMode: Int): Int {
    val value = memory[memoryAddress]

    return if (parameterMode == PARAMETER_MODE_POSITION) memory[value] else value
}

private fun setValue(memory: MutableList<Int>, memoryAddress: Int, value: Int) {
    memory[memory[memoryAddress]] = value
}
