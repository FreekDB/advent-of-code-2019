package com.github.freekdb.aoc2019

import java.io.File

private const val CENTER_OF_MASS = "COM"
private const val SANTA = "SAN"
private const val YOU = "YOU"

// https://adventofcode.com/2019/day/6
fun main() {
    val orbitMap = mutableMapOf<String, MutableList<String>>()
    val reverseOrbitMap = mutableMapOf<String, String>()

    File("input/day-06--input.txt")
        .readLines()
        .filter { it.isNotEmpty() }
        .map { it.split(")") }
        .forEach {
            val sourceObject = it[0]
            val destinationObject = it[1]

            val destinations = orbitMap.getOrPut(sourceObject, { mutableListOf() })
            destinations.add(destinationObject)

            reverseOrbitMap[destinationObject] = sourceObject
        }

    val orbitCountMap = mutableMapOf(CENTER_OF_MASS to 0)
    extendOrbitCountMap(
        CENTER_OF_MASS,
        orbitMap,
        orbitCountMap
    )

    // Part 1.
    println("Total number of direct and indirect orbits: ${orbitCountMap.values.sum()}.")

    // Part 2.
    val pathToYou = getPathToStart(YOU, reverseOrbitMap)
    val pathToSanta = getPathToStart(SANTA, reverseOrbitMap)
    val firstIntersection = pathToYou.intersect(pathToSanta).first()
    val minimumOrbitalTransfers = pathToYou.indexOf(firstIntersection) - 1 + pathToSanta.indexOf(firstIntersection) - 1
    println("Minimum number of orbital transfers: $minimumOrbitalTransfers")
}

fun extendOrbitCountMap(sourceObject: String, orbitMap: Map<String, List<String>>,
                        orbitCountMap: MutableMap<String, Int>) {
    val sourceOrbitCount = orbitCountMap[sourceObject] ?: 0
    val destinations = orbitMap[sourceObject]

    destinations?.forEach {
        orbitCountMap[it] = sourceOrbitCount + 1

        extendOrbitCountMap(it, orbitMap, orbitCountMap)
    }
}

fun getPathToStart(finalDestination: String, reverseOrbitMap: MutableMap<String, String>) =
    generateSequence(finalDestination) { objectName ->
        reverseOrbitMap[objectName].takeIf { it != CENTER_OF_MASS }
    }.toList()
