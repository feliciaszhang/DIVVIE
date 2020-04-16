package com.example.divvie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val listOfPrices = MutableLiveData<Array<Double>>()
    val listOfPricesObservable: LiveData<Array<Double>>
        get() = listOfPrices

    private val displayPrices = MutableLiveData<Boolean>()
    val displayPricesObservable: LiveData<Boolean>
        get() = displayPrices

    private val numberOfPeople = MutableLiveData<Int>()
    val numberOfPeopleObservable: LiveData<Int>
        get() = numberOfPeople

    private val subtotal = MutableLiveData<Double>()
    val subtotalObservable: LiveData<Double>
        get() = subtotal

    private val tax = MutableLiveData<Double>()
    val taxObservable: LiveData<Double>
        get() = tax

    fun setDisplayPrices(bool: Boolean) {
        displayPrices.value = bool
    }

    fun setNumberOfPeople(num: Int) {
        numberOfPeople.value = num
    }

    fun getNumberOfPeople(): Int? {
        return numberOfPeople.value
    }

    fun setSubtotal(num: Double) {
        subtotal.value = num
    }

    fun getSubtotal(): Double? {
        return subtotal.value
    }

    fun setTax(num: Double) {
        tax.value = num
    }

    fun getTax(): Double? {
        return tax.value
    }
}