package com.example.divvie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.divvie.ITEM
import com.example.divvie.ITEM_ID
import com.example.divvie.PRICE
import com.example.divvie.SPLIT_BETWEEN

@Entity(tableName = ITEM)
data class Item (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ITEM_ID) var itemId: Int = 0,
    @ColumnInfo(name = PRICE) var price: Double,
    @ColumnInfo(name = SPLIT_BETWEEN) var splitBetween: Int
)