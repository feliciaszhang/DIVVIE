package com.felinix.divvie.data

import androidx.room.*
import com.felinix.divvie.*
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
    @ColumnInfo(name = SUBTOTAL) var subtotal: BigDecimal? = null,
    @ColumnInfo(name = TAX) var tax: BigDecimal? = null,
    @ColumnInfo(name = TIP) var tip: BigDecimal? = null,
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

    fun getBaseSubtotal(): BigDecimal {
        return (subtotal ?: BigDecimal.ZERO) - getAcc()
    }

    private fun getAcc(): BigDecimal {
        var acc = BigDecimal.ZERO
        for (p in listOfPrices) {
            acc += p.acc
        }
        acc += tempPrice?.acc ?: BigDecimal.ZERO
        return acc
    }
}