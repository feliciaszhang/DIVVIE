package com.example.divvie.data

import androidx.room.*
import com.example.divvie.*
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = PERSON,
    indices = [Index(ID)]
)
data class Person (
    @PrimaryKey
    @ColumnInfo(name = ID) var id: Int,
    @ColumnInfo(name = NAME) var name: String = "",
    @ColumnInfo(name = SUBTOTAL) var subtotal: Double? = null,
    @ColumnInfo(name = TAX) var tax: Double? = null,
    @ColumnInfo(name = TIP) var tip: Double? = null,
    @ColumnInfo(name = TEMP_PRICE) var tempPrice: Price? = null,
    @ColumnInfo(name = LIST_OF_PRICES) var listOfPrices: ArrayDeque<Price> = ArrayDeque()
): Comparable<Person> {

    override fun compareTo(other: Person): Int {
        return when {
            this.getAcc() == other.getAcc() -> 0
            this.getAcc() < other.getAcc() -> -1
            else -> 1
        }
    }

    fun getBaseSubtotal(): Double {
        return ((subtotal?.toBigDecimal() ?: BigDecimal.ZERO) - getAcc().toBigDecimal()).toDouble()
    }

    private fun getAcc(): Double {
        var acc = BigDecimal.ZERO
        for (p in listOfPrices) {
            acc += p.acc.toBigDecimal()
        }
        acc += tempPrice?.acc?.toBigDecimal() ?: BigDecimal.ZERO
        return acc.toDouble()
    }
}