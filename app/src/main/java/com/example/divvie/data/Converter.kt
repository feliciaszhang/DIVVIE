package com.example.divvie.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*

class ListConverter {

    @TypeConverter
    fun listToJson(value: ArrayDeque<Price>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = ArrayDeque(Gson().fromJson(value, Array<Price>::class.java).toList())
}

class PriceConverter {

    @TypeConverter
    fun priceToJson(value: Price?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToPrice(value: String) = Gson().fromJson(value, Price::class.java)
}