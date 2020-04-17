package com.example.divvie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.divvie.*

@Entity(
    tableName = COUNTER,
    foreignKeys = [ForeignKey(
        entity = Item::class,
        parentColumns = arrayOf(ITEM_ID),
        childColumns = arrayOf(COUNTER_ITEM_ID),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Counter (
    @PrimaryKey
    @ColumnInfo(name = COUNTER_ITEM_ID) var itemId: Int,
    @ColumnInfo(name = SPLIT_BETWEEN) var count: Int
)