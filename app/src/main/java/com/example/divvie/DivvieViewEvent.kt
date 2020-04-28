package com.example.divvie

open class DivvieViewEvent

sealed class MainEvent: DivvieViewEvent() {
    object DisplayActivity: MainEvent()
}

sealed class InputViewEvent: DivvieViewEvent() {
    object DisplayFragment: InputViewEvent()
    object InsertPerson: InputViewEvent()
    object RemovePerson: InputViewEvent()
    data class EnterSubtotal(val input: String): InputViewEvent()
    data class EnterTax(val input: String): InputViewEvent()
    object Next: InputViewEvent()
}

sealed class SplitViewEvent: DivvieViewEvent() {
    object DisplayFragment: SplitViewEvent()
    object SplitEqually: SplitViewEvent()
    object EnterIndividually: SplitViewEvent()
    object Calculate: SplitViewEvent()
    object BackToInput: SplitViewEvent()
    object BackToItem: SplitViewEvent()
}

sealed class ItemViewEvent: DivvieViewEvent() {
    object DisplayFragment: ItemViewEvent()
    data class EnterItemPrice(val input: String): ItemViewEvent()
    object Next: ItemViewEvent()
    object Done: ItemViewEvent()
    object Back: ItemViewEvent()
    object Undo: ItemViewEvent()
    object ClearAll: ItemViewEvent()
}

sealed class ResultViewEvent: DivvieViewEvent() {
    object DisplayFragment: ResultViewEvent()
    data class EnterCurrencyTip(val input: String): ResultViewEvent()
    data class EnterPercentageTip(val input: String): ResultViewEvent()
    object SelectCurrency: ResultViewEvent()
    object SelectPercentage: ResultViewEvent()
    object Back: ResultViewEvent()
    object StartOver: ResultViewEvent()
}

sealed class BowlsViewEvent: DivvieViewEvent() {
    object DisplayFragment: BowlsViewEvent()
    data class ClickBowl(val i: Int): BowlsViewEvent()
    object DisplayBreakdown: BowlsViewEvent()
}