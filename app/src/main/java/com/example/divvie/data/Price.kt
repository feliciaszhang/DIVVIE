package com.example.divvie.data

import kotlin.math.round

data class Price(val base: Double, val acc: Double) {

    companion object {
        fun moneyDivider(dividend: Double, divisor: Int): Price {
            if (dividend == 0.0 || divisor == 0) {
                return Price(0.0, 0.0)
            }
            val roundedDividend = round(dividend * 100).toInt()
            val quotient = Math.floorDiv(roundedDividend, divisor)
            val remainder = roundedDividend % divisor
            return Price(quotient.toDouble() / 100, remainder.toDouble())
        }
    }
}