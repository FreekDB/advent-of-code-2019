package com.github.freekdb.aoc2019

import java.io.File

private const val OPCODE_PLUS = 1
private const val OPCODE_TIMES = 2
private const val OPCODE_HALT = 99

// https://adventofcode.com/2019/day/2
fun main() {
    val initialState = File("input/day-02--input.txt")
        .readLines()
        .filter { it.isNotEmpty() }
        .flatMap { it.split(",") }
        .map { it.toInt() }

    for (noun in 0..99) {
        for (verb in 0..99) {
            val state = listOf(initialState[0], noun, verb) + initialState.drop(3)
            val finalState = runProgram(state)
            if (finalState[0] == 19690720) {
                println("Answer $noun$verb gives finalState[0]: ${finalState[0]}")
            }
        }
    }
}

private fun runProgram(initialState: List<Int>): List<Int> {
    val memory = initialState.toMutableList()
    var instructionPointer = 0
    var opcode = memory[instructionPointer]

    while (opcode != OPCODE_HALT) {
        if (opcode == OPCODE_PLUS || opcode == OPCODE_TIMES) {
            val operand1 = memory[memory[instructionPointer + 1]]
            val operand2 = memory[memory[instructionPointer + 2]]
            val result = if (opcode == 1) operand1 + operand2 else operand1 * operand2
            memory[memory[instructionPointer + 3]] = result
        }

        instructionPointer += 4
        opcode = memory[instructionPointer]
    }

    return memory
}
