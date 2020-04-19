package com.example.divvie.database

import androidx.room.*
import com.example.divvie.*

@Entity(
    tableName = PERSON,
    indices = [Index(ID)]
)
data class Person (
    @PrimaryKey
    @ColumnInfo(name = ID) var id: Int,
    @ColumnInfo(name = SUBTOTAL) var subtotal: Double = AMOUNT_DEFAULT,
    @ColumnInfo(name = TAX) var tax: Double = AMOUNT_DEFAULT,
    @ColumnInfo(name = TIP) var tip: Double = AMOUNT_DEFAULT
)