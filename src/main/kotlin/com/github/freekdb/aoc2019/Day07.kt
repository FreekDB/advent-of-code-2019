package com.github.freekdb.aoc2019

import java.io.File

private const val PARAMETER_MODE_POSITION = 0
// private const val PARAMETER_MODE_IMMEDIATE = 1

// https://adventofcode.com/2019/day/7
fun main() {
    val amplifierControllerSoftware = File("input/day-07--input.txt")
        .readLines()[0]
        .split(",")
        .map { it.toInt() }

    permute(listOf(0, 1, 2, 3, 4)).forEach {
        println(it)
    }
    println()

    val amplifier = IntcodeComputer(amplifierControllerSoftware)
    amplifier.runProgram()
    println(amplifier.memory[0])
}

// Adapted from https://rosettacode.org/wiki/Permutations#Kotlin
fun <T> permute(input: List<T>): List<List<T>> {
    return if (input.size > 1) {
        val permutations = mutableListOf<List<T>>()

        val elementToInsert = input.first()
        for (subPermutation in permute(input.drop(1))) {
            for (insertIndex in 0..subPermutation.size) {
                val newPermutation = subPermutation.toMutableList()
                newPermutation.add(insertIndex, elementToInsert)
                permutations.add(newPermutation)
            }
        }

        permutations
    } else {
        listOf(input)
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


class IntcodeComputer(initialState: List<Int>) {
    val memory = initialState.toMutableList()

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
                Opcode.OPCODE_HALT -> println("Program is halting.")
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
        print("Please enter input value: ")
        val value = readLine()?.toInt() ?: 0

        setValue(instructionPointer + 1, value)
    }

    private fun printValueToOutput(instructionHeader: Int, instructionPointer: Int) {
        val value = getValue(instructionPointer + 1, (instructionHeader / 100) % 10)

        println("Output value: $value")
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
