package com.felili.divvie.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.math.BigDecimal
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

class BigDecimalConverter {

    @TypeConverter
    fun bigDecimalToString(input: BigDecimal?): String {
        return input?.toPlainString() ?: "null"
    }

    @TypeConverter
    fun stringToBigDecimal(input: String?): BigDecimal? {
        return if (input == "null" || input == null) {
            null
        } else {
            return input.toBigDecimal()
        }
    }

}