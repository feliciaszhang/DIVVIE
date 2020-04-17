package com.example.divvie.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.divvie.DIVVIEDATABASE



@Database(
    entities = [
        Person::class,
        Item::class,
        Counter::class],
    version = 1
)
abstract class DivvieDatabase : RoomDatabase() {

    abstract fun dao(): DivvieDao

    companion object {
        @Volatile private var INSTANCE: DivvieDatabase? = null

        fun getInstance(context: Context): DivvieDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                DivvieDatabase::class.java, DIVVIEDATABASE)
                .build()
    }
}