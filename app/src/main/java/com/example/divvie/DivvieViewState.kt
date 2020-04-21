package com.example.divvie

data class InputViewState(
    val numberOfPeople: Int = NUMBER_OF_PEOPLE_DEFAULT,
    val subtotal: Double = AMOUNT_DEFAULT,
    val tax: Double = AMOUNT_DEFAULT
)

