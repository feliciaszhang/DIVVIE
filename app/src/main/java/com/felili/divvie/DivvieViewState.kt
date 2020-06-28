package com.felili.divvie

import com.felili.divvie.data.Person
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

data class DivvieViewState (
    val subtotal: BigDecimal?,
    val isSubtotalEditing: Boolean,
    val tax: BigDecimal?,
    val isTaxEditing: Boolean,
    val isSplittingBowls: Boolean,
    val editableName: Boolean,
    val personList: Array<Person>,
    val tip: BigDecimal?,
    val isTipEditing: Boolean,
    val leftover: BigDecimal?,
    val isCurrencyTip: Boolean,
    val tempItemPrice: BigDecimal?,
    val tempItemListOfIndex: ArrayList<Int>,
    val itemList: ArrayDeque<BigDecimal>,
    val isItemEditing: Boolean,
    val isPersonalResult: Boolean,
    val personalBreakDownIndex: Int?,
    val invalidSubtotal: Boolean,
    val invalidTax: Boolean,
    val invalidItem: Boolean,
    val invalidCurrencyTip: Boolean
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
                tempItemPrice = null, tempItemListOfIndex = ArrayList(), itemList = ArrayDeque(),
                isItemEditing = false, isPersonalResult = false, personalBreakDownIndex = null,
                invalidSubtotal = false, invalidTax = false, invalidItem = false, invalidCurrencyTip = false
            )
        }
    }
}