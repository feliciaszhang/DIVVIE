package com.example.divvie

sealed class InputViewEvent {
    object DisplayFragment: InputViewEvent()
    object InsertPerson: InputViewEvent()
    object RemovePerson: InputViewEvent()
    data class EnterSubtotal(val input: String): InputViewEvent()
    data class EnterTax(val input: String): InputViewEvent()
    object Next: InputViewEvent()
}

sealed class SplitViewEvent {
    object DisplayFragment: SplitViewEvent()
    object SplitEqually: SplitViewEvent()
    object EnterIndividually: SplitViewEvent()
    object Back: SplitViewEvent()
}

sealed class ItemViewEvent {
    object DisplayFragment: ItemViewEvent()
    data class EnterItemPrice(val input: String): ItemViewEvent()
    object Next: ItemViewEvent()
    object Done: ItemViewEvent()
    object Back: ItemViewEvent()
    object ClearAll: ItemViewEvent()
}

sealed class ResultViewEvent {
    object DisplayFragment: ResultViewEvent()
    data class EnterTip(val input: String): ResultViewEvent()
    object ToggleFormat: ResultViewEvent()
    object Back: ResultViewEvent()
    object StartOver: ResultViewEvent()
}

sealed class BowlsViewEvent {
    object DisplayFragment: BowlsViewEvent()
    object DisplayBowls: BowlsViewEvent()
    object ClickBowl: BowlsViewEvent()
    object DisplayBreakdown: BowlsViewEvent()
}