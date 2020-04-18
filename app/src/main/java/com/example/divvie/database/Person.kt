package com.example.divvie.database

import androidx.room.*
import com.example.divvie.*

@Entity(
    tableName = PERSON,
    indices = [Index(INDEX)]
)
data class Person (
    @PrimaryKey
    @ColumnInfo(name = INDEX) var index: Int,
    @ColumnInfo(name = SUBTOTAL) var subtotal: Double = 0.0,
    @ColumnInfo(name = TAX) var tax: Double = 0.0,
    @ColumnInfo(name = TIP) var tip: Double = 0.0
)