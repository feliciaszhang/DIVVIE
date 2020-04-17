package com.example.divvie.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DivvieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(vararg person: Person)

    @Query("SELECT * FROM PERSON")
    fun getAllPerson(): Array<Person>

    @Delete
    fun deletePerson(vararg person: Person)

    @Query("SELECT COUNT(PERSON_ID) FROM PERSON")
    fun getNumberOfPeople(): LiveData<Int>
}