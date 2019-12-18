package com.github.freekdb.aoc2019

import java.io.File

private const val DIRECTION_UP = 'U'
private const val DIRECTION_DOWN = 'D'
private const val DIRECTION_LEFT = 'L'
private const val DIRECTION_RIGHT = 'R'

// Heavily inspired by Todd Ginsberg:
// https://github.com/tginsberg/advent-2019-kotlin/blob/master/src/main/kotlin/com/ginsberg/advent2019/Day03.kt

// https://adventofcode.com/2019/day/3
fun main(arguments: Array<String>) {
    val inputPath = if (arguments.isEmpty()) "input/day-03--input.txt" else arguments[0]
    findCrossingDistance(inputPath)
}

private fun findCrossingDistance(inputPath: String) {
    val inputLines =
        File(inputPath)
            .readLines()
            .filter { it.isNotEmpty() }

    val path1 = parsePath(inputLines[0])
    val path2 = parsePath(inputLines[1])
    val intersections = path1.intersect(path2)

    val crossingDistance = solvePart1(intersections)
    println("Crossing distance: $crossingDistance")

    val combinedSteps = solvePart2(intersections, path1, path2)
    println("Combined steps: $combinedSteps")
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
    intersections.map { it.distanceTo(Point2D.ORIGIN) }.min() ?: -1

private fun solvePart2(intersections: Set<Point2D>, path1: List<Point2D>, path2: List<Point2D>): Int =
    intersections.map { 2 + path1.indexOf(it) + path2.indexOf(it) }.min() ?: -1
