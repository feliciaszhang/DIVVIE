package com.example.divvie.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.divvie.DIVVIEDATABASE



@Database(
    entities = [Person::class],
    version = 1
)
abstract class DivvieDatabase : RoomDatabase() {

    abstract fun dao(): DivvieDao

    companion object {
        @Volatile private var INSTANCE: DivvieDatabase? = null

        fun getInstance(context: Context): DivvieDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    DivvieDatabase::class.java, DIVVIEDATABASE)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }
    }
}