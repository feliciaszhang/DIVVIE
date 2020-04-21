package com.example.divvie.viewModels

import android.app.Application
import com.example.divvie.*
import com.example.divvie.database.Person

class InputViewModel(
    app: Application,
    initialViewState: InputViewState
) : DivvieViewModel<InputViewState>(app, initialViewState) {

    fun onEvent(event: InputViewEvent) {
        when (event) {
            is InputViewEvent.InsertPerson -> onInsertPerson()
            is InputViewEvent.RemovePerson -> onRemovePerson()
            is InputViewEvent.EnterSubtotal -> onEnterSubtotal(event.sub)
            is InputViewEvent.EnterTax -> onEnterTax(event.tax)
        }
    }

    private fun onInsertPerson() {
        val num = getNumberOfPeopleStatic()
        if (num < MAX_NUMBER_OF_PEOPLE) {
            insertPerson(Person(id = num))
        }
    }

    private fun onRemovePerson() {
        val num = getNumberOfPeopleStatic()
        if (num > MIN_NUMBER_OF_PEOPLE) {
            deletePerson(Person(id = num - 1))
        }
    }

    private fun onEnterSubtotal(num: Double) {
        finalSubtotal = num
        currentViewState.copy(subtotal = num)
    }

    private fun onEnterTax(num: Double) {
        finalTax = num
        currentViewState.copy(tax = num)
    }
}