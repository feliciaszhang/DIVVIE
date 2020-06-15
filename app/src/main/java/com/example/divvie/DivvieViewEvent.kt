package com.example.divvie

sealed class DivvieViewEvent {
    object DisplayActivity: DivvieViewEvent()

    object DisplayInputFragment: DivvieViewEvent()
    object InputInsertPerson: DivvieViewEvent()
    object InputRemovePerson: DivvieViewEvent()
    data class InputEnterSubtotal(val input: String): DivvieViewEvent()
    data class InputEnterTax(val input: String): DivvieViewEvent()
    object InputToSplit: DivvieViewEvent()

    object DisplaySplitFragment: DivvieViewEvent()
    object SplitToResult: DivvieViewEvent()
    object SplitToItem: DivvieViewEvent()
    object SplitToInput: DivvieViewEvent()

    object DisplayItemFragment: DivvieViewEvent()
    data class ItemEnterPrice(val input: String): DivvieViewEvent()
    object ItemNext: DivvieViewEvent()
    object ItemDone: DivvieViewEvent()
    object ItemUndo: DivvieViewEvent()
    object ItemClear: DivvieViewEvent()
    object ItemToSplit: DivvieViewEvent()

    object DisplayCalculateFragment: DivvieViewEvent()
    object CalculateToItem: DivvieViewEvent()
    object CalculateToResult: DivvieViewEvent()

    object DisplayResultFragment: DivvieViewEvent()
    data class ResultEnterCurrencyTip(val input: String): DivvieViewEvent()
    data class ResultEnterPercentageTip(val input: String): DivvieViewEvent()
    object ResultSelectCurrency: DivvieViewEvent()
    object ResultSelectPercentage: DivvieViewEvent()
    object ResultToSplit: DivvieViewEvent()
    object ResultToInput: DivvieViewEvent()

    object DisplayBowlsFragment: DivvieViewEvent()
    data class BowlsEnterName(val i: Int, val input: String): DivvieViewEvent()
    data class BowlsSplitPrice(val i: Int): DivvieViewEvent()
    data class BowlsViewBreakdown(val i: Int?): DivvieViewEvent()
}