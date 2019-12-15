import kotlin.experimental.or
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val NUMBER_PATH_1 = 0
private const val NUMBER_PATH_2 = 1

private const val DIRECTION_UP = 'U'
private const val DIRECTION_DOWN = 'D'
private const val DIRECTION_LEFT = 'L'
private const val DIRECTION_RIGHT = 'R'

// https://adventofcode.com/2019/day/3
fun main(arguments: Array<String>) {
    val inputPath = if (arguments.isEmpty()) "actual-input.txt" else arguments[0]
    val crossingDistance = findCrossingDistance(inputPath)
    println("Crossing distance for input file $inputPath: $crossingDistance")
}

private fun findCrossingDistance(inputPath: String): Int {
    val inputLines = readInput(inputPath)

    return if (inputLines.size == 2) {
        val path1 = inputLines[0]
        val path2 = inputLines[1]

        val grid = Grid(mergeDimensions(calculateDimensions(path1), calculateDimensions(path2)))

        grid.registerPath(path1, NUMBER_PATH_1)
        grid.registerPath(path2, NUMBER_PATH_2)

        grid.findClosestDistance()
    } else {
        0
    }
}

private fun readInput(inputPath: String): List<String> {
    return object{}.
        javaClass
        .getResource(inputPath)
        .readText()
        .split("\n")
        .filter { it.isNotEmpty() }
}

private fun calculateDimensions(path: String): Dimensions {
    var rowIndex = 0
    var columnIndex = 0

    var minimumRowIndex = 0
    var maximumRowIndex = 0
    var minimumColumnIndex = 0
    var maximumColumnIndex = 0

    path
        .split(",")
        .forEach {
            val direction = it[0]
            val distance = it.substring(1).toInt()

            rowIndex += calculateVerticalDistance(direction, distance)
            columnIndex += calculateHorizontalDistance(direction, distance)

            minimumRowIndex = min(minimumRowIndex, rowIndex)
            maximumRowIndex = max(maximumRowIndex, rowIndex)
            minimumColumnIndex = min(minimumColumnIndex, columnIndex)
            maximumColumnIndex = max(maximumColumnIndex, columnIndex)
        }

    return Dimensions(minimumRowIndex, maximumRowIndex, minimumColumnIndex, maximumColumnIndex)
}

private fun calculateVerticalDistance(direction: Char, distance: Int): Int {
    return when (direction) {
        DIRECTION_UP -> -distance
        DIRECTION_DOWN -> distance
        else -> 0
    }
}

private fun calculateHorizontalDistance(direction: Char, distance: Int): Int = when (direction) {
    DIRECTION_LEFT -> -distance
    DIRECTION_RIGHT -> distance
    else -> 0
}


data class Dimensions(val minimumRowIndex: Int, val maximumRowIndex: Int,
                      val minimumColumnIndex: Int, val maximumColumnIndex: Int) {
    val height = maximumRowIndex - minimumRowIndex + 1
    val width = maximumColumnIndex - minimumColumnIndex + 1
}


fun mergeDimensions(dimensions1: Dimensions, dimensions2: Dimensions): Dimensions {
    return Dimensions(
        min(dimensions1.minimumRowIndex, dimensions2.minimumRowIndex),
        max(dimensions1.maximumRowIndex, dimensions2.maximumRowIndex),
        min(dimensions1.minimumColumnIndex, dimensions2.minimumColumnIndex),
        max(dimensions1.maximumColumnIndex, dimensions2.maximumColumnIndex)
    )
}


class Grid(dimensions: Dimensions) {
    private val height = dimensions.height + 2
    private val width = dimensions.width + 2
    private val originRowIndex = 1 - dimensions.minimumRowIndex
    private val originColumnIndex = 1 - dimensions.minimumColumnIndex

    private val cells: Array<Array<Byte>> = Array(height) { Array(width) { 0.toByte() } }

    fun registerPath(path: String, number: Int) {
        var rowIndex = originRowIndex
        var columnIndex = originColumnIndex

        path
            .split(",")
            .forEach {
                val direction = it[0]
                val distance = it.substring(1).toInt()
                val verticalDistance = calculateVerticalDistance(direction, distance)
                val horizontalDistance = calculateHorizontalDistance(direction, distance)

                markCells(rowIndex, columnIndex, verticalDistance, horizontalDistance, number)

                rowIndex += verticalDistance
                columnIndex += horizontalDistance
            }
    }

    private fun markCells(startRowIndex: Int, startColumnIndex: Int, verticalDistance: Int, horizontalDistance: Int,
                          number: Int) {
        // https://stackoverflow.com/questions/9562605/in-kotlin-can-i-create-a-range-that-counts-backwards
        val fromRowIndex = min(startRowIndex, startRowIndex + verticalDistance)
        val toRowIndex = max(startRowIndex, startRowIndex + verticalDistance)
        val fromColumnIndex = min(startColumnIndex, startColumnIndex + horizontalDistance)
        val toColumnIndex = max(startColumnIndex, startColumnIndex + horizontalDistance)

        for (rowIndex in fromRowIndex..toRowIndex) {
            for (columnIndex in fromColumnIndex..toColumnIndex) {
                cells[rowIndex][columnIndex] = cells[rowIndex][columnIndex] or (1 shl number).toByte()
            }
        }
    }

    fun findClosestDistance(): Int {
        var closestDistance = Int.MAX_VALUE
        val numberCrossing = ((1 shl NUMBER_PATH_1) or (1 shl NUMBER_PATH_2)).toByte()

        for (rowIndex in 0 until height) {
            for (columnIndex in 0 until width) {
                if (cells[rowIndex][columnIndex] == numberCrossing && isNotOrigin(rowIndex, columnIndex)) {
                    val distance = abs(rowIndex - originRowIndex) + abs(columnIndex - originColumnIndex)
                    closestDistance = min(closestDistance, distance)
                }
            }
        }

        return closestDistance
    }

    private fun isNotOrigin(rowIndex: Int, columnIndex: Int): Boolean =
        rowIndex != originRowIndex || columnIndex != originColumnIndex
}
