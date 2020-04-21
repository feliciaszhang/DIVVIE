package com.example.divvie

sealed class InputViewEvent {
    object InsertPerson: InputViewEvent()
    object RemovePerson: InputViewEvent()
    data class EnterSubtotal(val sub: Double): InputViewEvent()
    data class EnterTax(val tax: Double): InputViewEvent()
}

sealed class SplitViewEvent {
    object SplitEqually: SplitViewEvent()
    object SplitIndividually: SplitViewEvent()
    object Back: SplitViewEvent()
}

sealed class ItemViewEvent {
    object EnterItemPrice: ItemViewEvent()
    object Next: ItemViewEvent()
    object Done: ItemViewEvent()
    object Back: ItemViewEvent()
    object ClearAll: ItemViewEvent()
}

sealed class ResultViewEvent {
    object EnterTip: ResultViewEvent()
    object ToggleFormat: ResultViewEvent()
    object Back: ResultViewEvent()
    object StartOver: ResultViewEvent()
}

sealed class BowlsViewEvent {
    object DisplayBowls: BowlsViewEvent()
    object ClickBowl: BowlsViewEvent()
    object DisplayBreakdown: BowlsViewEvent()
}