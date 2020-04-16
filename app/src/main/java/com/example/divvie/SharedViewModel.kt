package com.example.divvie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val numberOfPeople = MutableLiveData<Int>()
    val numberOfPeopleObservable: LiveData<Int>
        get() = numberOfPeople

    private val subtotal = MutableLiveData<Double>()
    val subtotalObservable: LiveData<Double>
        get() = subtotal

    private val tax = MutableLiveData<Double>()
    val taxObservable: LiveData<Double>
        get() = tax

    fun setNumberOfPeople(num: Int) {
        numberOfPeople.value = num
    }

    fun setSubtotal(num: Double) {
        subtotal.value = num
    }

    fun setTax(num: Double) {
        tax.value = num
    }
}