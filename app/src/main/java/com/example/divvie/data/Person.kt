package com.example.divvie.data

import androidx.room.*
import com.example.divvie.*

@Entity(
    tableName = PERSON,
    indices = [Index(ID)]
)
data class Person (
    @PrimaryKey
    @ColumnInfo(name = ID) var id: Int,
    @ColumnInfo(name = SUBTOTAL) var personalSubtotal: Double? = null,
    @ColumnInfo(name = TAX) var personalTax: Double? = null,
    @ColumnInfo(name = TIP) var personalTip: Double? = null,
    @ColumnInfo(name = TEMP_PRICE) var personalTempPrice: Double? = null
)