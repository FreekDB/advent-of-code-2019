package com.github.freekdb.aoc2019

import kotlin.math.abs

// Heavily inspired by Todd Ginsberg:
// https://github.com/tginsberg/advent-2019-kotlin/blob/master/src/main/kotlin/com/ginsberg/advent2019/Points.kt
data class Point2D(val x: Int, val y: Int) {
    companion object {
        val ORIGIN = Point2D(0, 0)
    }

    fun up(): Point2D = copy(y = y + 1)
    fun down(): Point2D = copy(y = y - 1)
    fun left(): Point2D = copy(x = x - 1)
    fun right(): Point2D = copy(x = x + 1)

    fun distanceTo(other: Point2D): Int =
        abs(x - other.x) + abs(y - other.y)
}
