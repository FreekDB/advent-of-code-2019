package com.github.freekdb.aoc2019

private const val DIRECTION_UP = 'U'
private const val DIRECTION_DOWN = 'D'
private const val DIRECTION_LEFT = 'L'
private const val DIRECTION_RIGHT = 'R'

// Heavily inspired by Todd Ginsberg:
// https://github.com/tginsberg/advent-2019-kotlin/blob/master/src/main/kotlin/com/ginsberg/advent2019/Day03.kt

// https://adventofcode.com/2019/day/3
fun main(arguments: Array<String>) {
    val inputPath = if (arguments.isEmpty()) "actual-input.txt" else arguments[0]
    val crossingDistance = findCrossingDistance(inputPath)
    println("Crossing distance for input file $inputPath: $crossingDistance")
}

private fun findCrossingDistance(inputPath: String): Int {
    val inputLines = readInput(inputPath)
    val path1 = parsePath(inputLines[0])
    val path2 = parsePath(inputLines[1])
    val intersections = path1.intersect(path2)

    return solvePart1(intersections)
}

private fun readInput(inputPath: String): List<String> {
    return object{}
        .javaClass
        .classLoader
        .getResource(inputPath)
        ?.readText()
        ?.split("\n")
        ?.filter { it.isNotEmpty() }
        ?: throw IllegalArgumentException("Cannot read from input file $inputPath")
}

private fun parsePath(path: String): List<Point2D> {
    var currentPoint = Point2D.ORIGIN

    return path.split(",").flatMap {
        val direction = it.first()
        val distance = it.drop(1).toInt()

        val segment = (0 until distance).map {
            val nextPoint = when (direction) {
                DIRECTION_UP -> currentPoint.up()
                DIRECTION_DOWN -> currentPoint.down()
                DIRECTION_LEFT -> currentPoint.left()
                DIRECTION_RIGHT -> currentPoint.right()
                else -> throw IllegalArgumentException("Invalid direction: $direction")
            }

            currentPoint = nextPoint

            nextPoint
        }

        segment
    }
}

private fun solvePart1(intersections: Set<Point2D>): Int =
    intersections.map { it.distanceTo(Point2D.ORIGIN) }.min()!!
