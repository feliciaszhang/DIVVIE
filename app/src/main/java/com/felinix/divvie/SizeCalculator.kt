package com.felinix.divvie

class SizeCalculator(private val size: Float) {
//    (0, 5)
//    (1, 10)
//    (2, 6)
//    (3, 6)
//    (4, 6)
//    (5, 6)
//    (6, 5)
//    (7, 6)
//    (8, 5)
//    (9, 5)

    private fun count(text: String): Int {
        var acc = 0
        for (i in text.indices) {
           acc = when (text[i]) {
               '0', '6', '8', '9' -> acc + 6            // 30/5
               '1' -> acc + 3                           // 30/10
               '2', '3', '4', '5', '7' -> acc + 5       // 30/6
               else -> acc + 0
           }
        }
        return acc
    }

    fun resize(text: String): Float {
        return when {
            (count(text) <= 30) -> { size }
            (count(text) <= 33) -> { size - 3 }
            (count(text) <= 36) -> { size - 5 }
            (count(text) <= 39) -> { size - 7 }
            (count(text) <= 42) -> { size - 9 }
            (count(text) <= 45) -> { size - 11 }
            (count(text) <= 48) -> { size - 13 }
            (count(text) <= 51) -> { size - 15 }
            (count(text) <= 54) -> { size - 17 }
            (count(text) <= 57) -> { size - 19 }
            else -> { size - 20 }
        }
    }
}