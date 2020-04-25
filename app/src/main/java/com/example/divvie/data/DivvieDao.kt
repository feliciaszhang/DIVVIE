package com.example.divvie.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DivvieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(vararg person: Person)

    @Query("SELECT * FROM PERSON ORDER BY ID ASC")
    fun getAllPersonStatic(): Array<Person>

    @Query("SELECT * FROM PERSON WHERE ID = :id")
    fun findPerson(id: Int): Person

    @Query("SELECT * from PERSON ORDER BY ID ASC")
    fun getAllPerson() : LiveData<List<Person>>

    @Delete
    fun deletePerson(vararg person: Person)

    @Query("SELECT COUNT(*) FROM PERSON")
    fun getNumberOfPeople(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM PERSON")
    fun getNumberOfPeopleStatic(): Int

    @Update
    fun updatePerson(vararg person: Person)
}