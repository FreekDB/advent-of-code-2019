package com.github.freekdb.aoc2019

import java.io.File

fun main() {
    val totalRequiredFuel = File("input/day-01--actual-input.txt")
        .readLines()
        .filter { it.isNotEmpty() }
        .map { requiredFuel(it.toInt()) }
        .sum()

    println("Total required fuel: $totalRequiredFuel.")
}

private fun requiredFuel(mass: Int): Int =
    generateSequence(requiredFuelExclusive(mass)) {
        val additionalFuel = requiredFuelExclusive(it)
        if (additionalFuel > 0) additionalFuel else null
    }.sum()

private fun requiredFuelExclusive(mass: Int): Int =
    (mass / 3) - 2
