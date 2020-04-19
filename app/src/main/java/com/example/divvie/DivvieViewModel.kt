package com.example.divvie

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.divvie.database.DivvieDatabase
import com.example.divvie.database.Person

class DivvieViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DivvieDatabase.getInstance(application).dao()

    private fun getAllPersonStatic() = dao.getAllPersonStatic()

    fun getAllPerson() = dao.getAllPerson()

    fun insertPerson(person: Person) { dao.insertPerson(person) }

    fun deletePerson(person: Person) = dao.deletePerson(person)

    fun getNumberOfPeople() = dao.getNumberOfPeople()

    fun updatePerson(person: Person) {dao.updatePerson(person)}

    fun splitPretaxEqually() {
        for (person in getAllPersonStatic()) {
            person.subtotal = getSubtotal()?.div(getAllPersonStatic().size) ?: AMOUNT_DEFAULT
            updatePerson(person)
        }
    }

    fun clearPersonalSubtotal() {
        for (person in getAllPersonStatic()) {
            person.subtotal = AMOUNT_DEFAULT
            person.tax = AMOUNT_DEFAULT
            person.tip = AMOUNT_DEFAULT
            updatePerson(person)
        }
    }

    fun calculatePersonResult() {
        for (person in getAllPersonStatic()) {
            val tax: Double = getTax() ?: AMOUNT_DEFAULT
            val tip: Double = getTip() ?: AMOUNT_DEFAULT
            val ratio = person.subtotal / getSubtotal()!!
            person.tax = ratio * tax
            person.tip = ratio * tip
            updatePerson(person)
        }
    }

    private val currentItemPrice = MutableLiveData<Double>()
    val currentItemPriceObservable: LiveData<Double>
        get() = currentItemPrice

    private val displayPrices = MutableLiveData<Boolean>()
    val displayPricesObservable: LiveData<Boolean>
        get() = displayPrices

    private val enterPrice = MutableLiveData<Boolean>()
    val enterPriceObservable: LiveData<Boolean>
        get() = enterPrice

    private val subtotal = MutableLiveData<Double>()

    private val tax = MutableLiveData<Double>()

    private val tip = MutableLiveData<Double>()

    private val total = MutableLiveData<Double>()
    val totalObservable: LiveData<Double>
        get() = total

    private fun setTotal() {
        val subtotal: Double = getSubtotal() ?: AMOUNT_DEFAULT
        val tax: Double = getTax() ?: AMOUNT_DEFAULT
        val tip: Double = getTip() ?: AMOUNT_DEFAULT
        total.value = subtotal + tax + tip
    }

    fun setCurrentItemPrice(num: Double) {
        currentItemPrice.value = num
    }

    fun setDisplayPrices(bool: Boolean) {
        displayPrices.value = bool
    }

    fun setEnterPrice(bool: Boolean) {
        enterPrice.value = bool
    }

    fun setSubtotal(num: Double) {
        subtotal.value = num
        setTotal()
    }

    fun getSubtotal(): Double? {
        return subtotal.value
    }

    fun setTax(num: Double) {
        tax.value = num
        setTotal()
    }

    fun getTax(): Double? {
        return tax.value
    }

    fun setTip(num: Double) {
        tip.value = num
        setTotal()
    }

    private fun getTip(): Double? {
        return tip.value
    }
}

