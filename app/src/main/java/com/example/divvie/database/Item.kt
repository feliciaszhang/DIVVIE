package com.example.divvie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.divvie.ITEM
import com.example.divvie.ITEM_ID
import com.example.divvie.PRICE

@Entity(tableName = ITEM)
data class Item (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ITEM_ID) var itemId: Int,
    @ColumnInfo(name = PRICE) var price: Double
)