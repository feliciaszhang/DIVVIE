package com.example.divvie

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

open class DivvieViewModel<ViewState>(
    app: Application,
    initialViewState: ViewState
) : AndroidViewModel(app) {

    private val viewState = MutableLiveData<ViewState>()
    val viewStateObservable: LiveData<ViewState>
        get() = viewState

    protected var currentViewState: ViewState = initialViewState
        set(value) {
            field = value
            viewState.value = value
        }
}