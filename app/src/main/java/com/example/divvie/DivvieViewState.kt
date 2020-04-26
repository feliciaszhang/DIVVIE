package com.example.divvie

import com.example.divvie.data.Item
import com.example.divvie.data.Person
import java.util.*
import kotlin.collections.ArrayList

data class DivvieViewState (
    var subtotal: Double = 0.0,
    var tax: Double = 0.0,
    var tip: Double = 0.0,
    var leftover: Double? = null,
    var isClickableBowls: Boolean = false,
    var personList: Array<Person> = Array(NUMBER_OF_PEOPLE_DEFAULT, {i -> Person(id = i)}),
    var isCurrencyTip: Boolean = true,
    var tempItemBasePrice: Double = 0.0,
    var tempItemFinalSplitPrice: Double = 0.0,
    var tempItemTempSplitPrice: Double = 0.0,
    var tempItemListOfIndex: ArrayList<Int> = ArrayList(),
    var itemStack: Stack<Item> = Stack()
)