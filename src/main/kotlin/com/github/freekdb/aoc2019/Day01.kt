package com.github.freekdb.aoc2019

import java.io.File

// https://adventofcode.com/2019/day/1
fun main() {
    val totalRequiredFuel =
        File("input/day-01--input.txt")
        .readLines()
        .filter { it.isNotEmpty() }
        .map { requiredFuel(it.toInt()) }
        .sum()

    println("Total required fuel: $totalRequiredFuel.")
}

private fun requiredFuel(mass: Int): Int =
    generateSequence(requiredFuelExclusive(mass)) { fuel ->
        requiredFuelExclusive(fuel).takeIf { it > 0 }
    }.sum()

@Suppress("unused")
private fun requiredFuelRecursive(mass: Int): Int {
    val fuel = requiredFuelExclusive(mass)
    return if (fuel > 0) fuel + requiredFuelRecursive(fuel) else 0
}

private fun requiredFuelExclusive(mass: Int): Int =
    (mass / 3) - 2
