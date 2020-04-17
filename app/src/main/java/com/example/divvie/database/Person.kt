package com.example.divvie.database

import androidx.room.*
import com.example.divvie.*

@Entity(
    tableName = PERSON,
    indices = [Index(PERSON_ITEM_ID)],
    foreignKeys = [ForeignKey(
        entity = Item::class,
        parentColumns = arrayOf(ITEM_ID),
        childColumns = arrayOf(PERSON_ITEM_ID),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Person (
    @ColumnInfo(name = PERSON_ITEM_ID) var itemId: Int? = null,
    @PrimaryKey
    @ColumnInfo(name = PERSON_ID) var personId: Int
)