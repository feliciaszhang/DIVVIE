package com.example.divvie.data

import com.example.divvie.AMOUNT_DEFAULT

data class Item (
    var basePrice: Double = AMOUNT_DEFAULT,
    var splitPrice: Double? = null
)