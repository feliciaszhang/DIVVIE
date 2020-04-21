package com.example.divvie

import android.graphics.Color
import com.example.divvie.database.Item

data class InputViewState(
    val numberOfPeople: Int = NUMBER_OF_PEOPLE_DEFAULT,
    val subtotal: Double = AMOUNT_DEFAULT,
    val tax: Double = AMOUNT_DEFAULT
)

data class ItemViewState(
    val tempItemPrice: Double = AMOUNT_DEFAULT,
    val tempLeftOverPrice: Double,
    val currentItem: Item,
    val finalLeftOverPrice: Double
)

data class ResultViewState(
    val total: Double,
    val tip: Double = AMOUNT_DEFAULT,
    val format: Int = 0
)

data class BowlsViewState(
    val isClickable: Boolean = false,
    val displayPrices: Boolean = false,
    val numberOfBowls: Int = NUMBER_OF_PEOPLE_DEFAULT
)

