package com.example.divvie.data

import androidx.room.*
import com.example.divvie.*
import java.io.Serializable

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
    @ColumnInfo(name = TEMP_PRICE) var tempPrice: Double? = null
): Serializable