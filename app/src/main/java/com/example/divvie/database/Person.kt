package com.example.divvie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.divvie.*

@Entity(
    tableName = PERSON,
    foreignKeys = [ForeignKey(
        entity = Item::class,
        parentColumns = arrayOf(ITEM_ID),
        childColumns = arrayOf(PERSON_ITEM_ID),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Person (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PERSON_ITEM_ID) var itemId: Int,
    @ColumnInfo(name = PERSON_ID) var personId: Int
)