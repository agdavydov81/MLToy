package com.example.test001

import kotlin.math.min
import kotlin.math.max
import kotlin.math.sqrt

class StatInfo {
    var min: Double = Double.NaN
        private set

    var max: Double = Double.NaN
        private set

    var sum: Double = 0.0
        private set

    var sumSq: Double = 0.0
        private set

    var count: Long = 0
        private set

    val mean: Double
        get() = sum / count

    val variance: Double
        get() {
            return max(0.0, sumSq - sum * sum / count) / (count - 1)
        }

    val std: Double
        get() = sqrt(variance)

    fun add(value: Double) {
        min = min(value, min)
        max = max(value, max)
        sum += value
        sumSq += value * value
        count++
    }
}