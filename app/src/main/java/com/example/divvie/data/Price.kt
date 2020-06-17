package com.example.divvie.data

data class Price(val base: Double, val acc: Double) {

    companion object {
        fun moneyDivider(dividend: Double, divisor: Int): Price {
            if (dividend == 0.0 || divisor == 0) {
                return Price(0.0, 0.0)
            }
            val roundedDividend = (dividend.toBigDecimal() * 100.toBigDecimal()).toBigInteger()
            val quotient = roundedDividend.divide(divisor.toBigInteger())
            val remainder = roundedDividend.remainder(divisor.toBigInteger())
            return Price(quotient.toDouble() / 100, remainder.toDouble())
        }
    }
}