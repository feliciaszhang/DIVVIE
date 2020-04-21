package com.example.divvie.viewModels

import android.app.Application
import com.example.divvie.*

class BowlsViewModel(
    app: Application,
    initialViewState: BowlsViewState
) : DivvieViewModel<BowlsViewState>(app, initialViewState) {

    fun onEvent(event: BowlsViewEvent) {
        when (event) {
            is BowlsViewEvent.DisplayBowls -> onDisplayBowls()
            is BowlsViewEvent.ClickBowl -> onClickBowl()
            is BowlsViewEvent.DisplayBreakdown -> onDisplayBreakdown()
        }
    }

    private fun onDisplayBowls() {
        currentViewState.copy(numberOfBowls = getNumberOfPeopleStatic())
    }

    private fun onClickBowl() {}
    private fun onDisplayBreakdown() {}
}