package com.felinix.divvie.data

import java.math.BigDecimal

data class Price(val base: BigDecimal, val acc: BigDecimal) {

    companion object {
        fun moneyDivider(dividend: BigDecimal, divisor: Int): Price {
            if (dividend == BigDecimal.ZERO || divisor == 0) {
                return Price(BigDecimal.ZERO, BigDecimal.ZERO)
            }
            val roundedDividend = (dividend * 100.toBigDecimal()).toBigInteger()
            val quotient = roundedDividend.divide(divisor.toBigInteger()).toBigDecimal()
            val remainder = roundedDividend.remainder(divisor.toBigInteger())
            return Price((quotient.divide(100.toBigDecimal())), remainder.toBigDecimal())
        }
    }
}