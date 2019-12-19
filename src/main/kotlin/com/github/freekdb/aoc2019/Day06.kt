package com.github.freekdb.aoc2019

import java.io.File

private const val CENTER_OF_MASS = "COM"

// https://adventofcode.com/2019/day/6
fun main() {
    val orbitMap = mutableMapOf<String, MutableList<String>>()

    File("input/day-06--input.txt")
        .readLines()
        .filter { it.isNotEmpty() }
        .map { it.split(")") }
        .forEach {
            val sourceObject = it[0]
            val destinationObject = it[1]

            val destinations = orbitMap.getOrPut(sourceObject, { mutableListOf() })
            destinations.add(destinationObject)
        }

    val orbitCountMap = mutableMapOf(CENTER_OF_MASS to 0)
    extendOrbitCountMap(CENTER_OF_MASS, orbitMap, orbitCountMap)

    println("Total number of direct and indirect orbits: ${orbitCountMap.values.sum()}.")
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
