package com.github.freekdb.aoc2019

import java.io.File

// https://adventofcode.com/2019/day/4
fun main() {
    val bounds =
        File("input/day-04--input.txt")
            .readLines()
            .first()
            .split("-")

    val lowerBound = bounds[0].toInt()
    val upperBound = bounds[1].toInt()

    generatePossiblePasswords(lowerBound, upperBound)

    println("Possible passwords count -- part 1: ${countPasswordsPart1(lowerBound, upperBound)}")
    println("Possible passwords count -- part 2: ${countPasswordsPart2(lowerBound, upperBound)}")
}

// Brute force approach: generate possible passwords that meet the general criteria.
private fun generatePossiblePasswords(lowerBound: Int, upperBound: Int): Sequence<List<Int>> {
    // General citeria:
    // - Passwords are six-digit numbers.
    // - Each number is within the range given in your puzzle input.
    // - Going from left to right, the digits never decrease; they only ever increase or stay the same
    //   (like 111123 or 135679).

    return sequence {
        for (digit1 in 0..9) {
            for (digit2 in digit1..9) {
                for (digit3 in digit2..9) {
                    for (digit4 in digit3..9) {
                        for (digit5 in digit4..9) {
                            for (digit6 in digit5..9) {
                                val number = "$digit1$digit2$digit3$digit4$digit5$digit6".toInt()

                                if (number in lowerBound..upperBound) {
                                    yield(listOf(digit1, digit2, digit3, digit4, digit5, digit6))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun countPasswordsPart1(lowerBound: Int, upperBound: Int): Int {
    // Additional rule:
    // - Two adjacent digits are the same (like 22 in 122345).
    //   -> We could generate five-digit numbers and "double" one of the five digits. This gives up to five possible
    //      six-digit passwords for each five-digit number.

    return generatePossiblePasswords(lowerBound, upperBound)
        .filter { it.toSet().size < 6 }
        .count()
}

private fun countPasswordsPart2(lowerBound: Int, upperBound: Int): Int {
    // Additional rules:
    // - Two adjacent digits are the same (like 22 in 122345).
    //   -> We could generate five-digit numbers and "double" one of the five digits. This gives up to five possible
    //      six-digit passwords for each five-digit number.
    // - The two adjacent matching digits are not part of a larger group of matching digits.

    return generatePossiblePasswords(lowerBound, upperBound)
        .filter { digits ->
            digits
                .groupingBy { digit -> digit }
                .eachCount()
                .values
                .contains(2)
        }
        .count()
}
