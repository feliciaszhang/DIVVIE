package com.example.divvie.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*

class ListConverter {

    @TypeConverter
    fun listToJson(value: ArrayDeque<Double>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = ArrayDeque(Gson().fromJson(value, Array<Double>::class.java).toList())
}