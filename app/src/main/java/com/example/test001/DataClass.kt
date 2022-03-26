package com.example.test001

import java.util.*

class DataClass(var name: String, var label: LabelInfo, var color: Int) {
    companion object {
        const val DIMENSION = 2
    }

    var points: DoubleArray = DoubleArray(1024 * DIMENSION)

    var numPoints: Int = 0

    fun add(x: Double, y: Double) {
        if ((numPoints + 1) * DIMENSION > points.size)
            points = Arrays.copyOf(points, (numPoints * 3 / 2 + 1) * DIMENSION)
        val offset = numPoints * DIMENSION
        points[offset] = x
        points[offset + 1] = y
        numPoints++
    }
}
