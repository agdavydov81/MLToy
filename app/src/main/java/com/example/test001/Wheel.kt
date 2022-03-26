package com.example.test001

class Wheel<T>(val values: List<T>) {
    private var index: Int = 0

    val next: T
        get() {
            val ret = values[index++]
            if (index >= values.size)
                index = 0
            return ret
        }
}