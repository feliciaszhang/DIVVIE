package com.example.divvie.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DivvieDao {

//    @Query("SELECT COUNT FROM COUNTER WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT COUNT(PERSON_ID) FROM PERSON")
    fun countNumberOfPeople(): LiveData<Int>
//
//    @Query("SELECT SPLIT_BETWEEN FROM COUNTER WHERE COUNTER_ITEM_ID='itemId'")
//    fun getCount(itemId: Int): LiveData<Int>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertItem(vararg item: Item)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertItemToCounter(vararg item: Item)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertItemToPerson(vararg item: Item)
//
//    @Delete
//    fun deleteItem(item: Item)
}