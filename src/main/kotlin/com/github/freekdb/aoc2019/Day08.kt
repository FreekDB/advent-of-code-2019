package com.github.freekdb.aoc2019

import java.io.File

// https://adventofcode.com/2019/day/8
fun main() {
    val input = File("input/day-08--input.txt").readLines()[0]

    println("Input length: ${input.length}.")
    println("15000 == 9925 + 2524 + 2551 = ${9925 + 2524 + 2551}")

    val width = 25
    val height = 6
    val layerSize = width * height

    solvePart1(input, layerSize)

    solvePart2(input, width, height, layerSize)
}


enum class DigitColor(val value: Char) {
    BLACK('0'),
    WHITE('1'),
    TRANSPARENT('2')
}


@Suppress("SameParameterValue")
private fun solvePart1(input: String, layerSize: Int) {
    val digitFrequencies: Map<Char, Int>? = input
        .chunked(layerSize)
        .map { layerDigits -> layerDigits.toList().groupingBy { it }.eachCount() }
        .minBy { it[DigitColor.BLACK.value] ?: 0 }

    println("Digit frequencies in layer with fewest '${DigitColor.BLACK.value}' digits: $digitFrequencies.")

    if (digitFrequencies != null) {
        val product = (digitFrequencies[DigitColor.WHITE.value] ?: 0) *
                (digitFrequencies[DigitColor.TRANSPARENT.value] ?: 0)

        println("Calculation -- '${DigitColor.WHITE.value}' frequency times" +
                " '${DigitColor.TRANSPARENT.value}' frequency =>")

        println("${digitFrequencies[DigitColor.WHITE.value]} * ${digitFrequencies[DigitColor.TRANSPARENT.value]} =" +
                " $product.")
    }

    println()
}

fun solvePart2(input: String, width: Int, height: Int, layerSize: Int) {
    for (rowIndex in 0 until height) {
        for (columnIndex in 0 until width) {
            var digitIndex = rowIndex * width + columnIndex
            var digit = DigitColor.TRANSPARENT.value

            while (digit == DigitColor.TRANSPARENT.value && digitIndex < input.length) {
                digit = input[digitIndex]
                digitIndex += layerSize
            }

            // For enhanced readability, I replace all black positions with spaces.
            print(if (digit == DigitColor.BLACK.value) ' ' else digit)
        }

        println()
    }
}
