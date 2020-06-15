package com.example.divvie

import com.example.divvie.data.Person
import java.util.*
import kotlin.collections.ArrayList

data class DivvieViewState (
    val subtotal: Double?,
    val isSubtotalEditing: Boolean,
    val tax: Double?,
    val isTaxEditing: Boolean,
    val isSplittingBowls: Boolean,
    val editableName: Boolean,
    val personList: Array<Person>,
    val tip: Double?,
    val isTipEditing: Boolean,
    val leftover: Double?,
    val isCurrencyTip: Boolean,
    val tempItemPrice: Double,
    val tempItemListOfIndex: ArrayList<Int>,
    val itemList: ArrayDeque<Double>,
    val isItemEditing: Boolean,
    val isPersonalResult: Boolean,
    val personalBreakDownIndex: Int?
) {
    companion object {
        private fun defaultPersonList(): Array<Person> {
            return Array(GUESTS_DEFAULT, { i -> Person(id = i) })
        }

        fun defaultViewState(): DivvieViewState {
            return DivvieViewState(
                subtotal = null, isSubtotalEditing = false, tax = null, isTaxEditing = false,
                isSplittingBowls = false, editableName = true, personList = defaultPersonList(),
                tip = null, isTipEditing = false, leftover = null, isCurrencyTip = true,
                tempItemPrice = 0.0, tempItemListOfIndex = ArrayList(), itemList = ArrayDeque(),
                isItemEditing = false, isPersonalResult = false, personalBreakDownIndex = null
            )
        }
    }
}