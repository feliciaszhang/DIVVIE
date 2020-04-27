package com.example.divvie.data

data class Item (
    var basePrice: Double = 0.0,
    var finalSplitPrice: Double = 0.0,
    var listOfIndex: ArrayList<Int> = ArrayList()
)