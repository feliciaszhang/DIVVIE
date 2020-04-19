package com.example.divvie.database

data class Item (
    val listOfIndex: HashSet<Int> = hashSetOf(),
    val splitPrice: Double? = null
)