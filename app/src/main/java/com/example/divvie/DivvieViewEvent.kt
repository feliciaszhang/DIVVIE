package com.example.divvie

sealed class InputViewEvent {
    object InsertPerson : InputViewEvent()
    object RemovePerson : InputViewEvent()
    object EnterSubtotal : InputViewEvent()
    object EnterTax : InputViewEvent()
    object Next : InputViewEvent()
}