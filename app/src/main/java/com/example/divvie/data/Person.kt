package com.example.divvie.data

import androidx.room.*
import com.example.divvie.*
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
    @ColumnInfo(name = TEMP_PRICE) var tempPrice: Double? = null,
    @ColumnInfo(name = LIST_OF_PRICES) var listOfPrices: ArrayDeque<Double> = ArrayDeque(),
    @ColumnInfo(name = ACC) var acc: Int = 0
): Comparable<Person> {

    override fun compareTo(other: Person): Int {
        return when {
            this.acc == other.acc -> 0
            this.acc < other.acc -> -1
            else -> 1
        }
    }
}