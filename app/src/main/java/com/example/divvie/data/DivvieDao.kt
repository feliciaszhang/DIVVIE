package com.example.divvie.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DivvieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(vararg person: Person)

    @Query("SELECT * FROM PERSON")
    fun getAllPersonStatic(): Array<Person>

    @Query("SELECT * FROM PERSON WHERE ID = :id")
    fun findPerson(id: Int): Person

    @Query("SELECT * from PERSON ORDER BY ID ASC")
    fun getAllPerson() : LiveData<List<Person>>

    @Delete
    fun deletePerson(vararg person: Person)

    @Query("DELETE FROM PERSON")
    fun deleteAllPerson()

    @Query("SELECT COUNT(*) FROM PERSON")
    fun getGuests(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM PERSON")
    fun getGuestsStatic(): Int

    @Update
    fun updatePerson(vararg person: Person)
}