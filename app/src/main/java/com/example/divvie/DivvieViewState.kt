package com.example.divvie

import com.example.divvie.data.Item
import com.example.divvie.data.Person
import java.util.*
import kotlin.collections.ArrayList

data class DivvieViewState (
    val subtotal: Double? = null,
    val isSubtotalEditing: Boolean = false,
    val tax: Double? = null,
    val isTaxEditing: Boolean = false,
    val isClickableBowls: Boolean = false,
    val personList: Array<Person> = Array(GUESTS_DEFAULT, { i -> Person(id = i) }),
    val tip: Double? = null,
    val isTipEditing: Boolean = false,
    val leftover: Double? = null,
    val isCurrencyTip: Boolean = true,
    val tempItemBasePrice: Double = 0.0,
    val tempItemFinalSplitPrice: Double = 0.0,
    val tempItemTempSplitPrice: Double = 0.0,
    val tempItemListOfIndex: ArrayList<Int> = ArrayList(),
    val itemStack: Stack<Item> = Stack()
)