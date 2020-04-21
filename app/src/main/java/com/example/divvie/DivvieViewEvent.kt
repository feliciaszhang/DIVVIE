package com.example.divvie

sealed class InputViewEvent {
    object InsertPerson: InputViewEvent()
    object RemovePerson: InputViewEvent()
    object EnterSubtotal: InputViewEvent()
    object EnterTax: InputViewEvent()
    object Next: InputViewEvent()
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
    object ClickBowl: BowlsViewEvent()
    object DisplayBreakdown: BowlsViewEvent()
}