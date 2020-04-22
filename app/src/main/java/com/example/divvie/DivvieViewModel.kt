package com.example.divvie

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.divvie.data.DivvieDatabase
import com.example.divvie.data.Item
import com.example.divvie.data.Person
import java.util.*
import kotlin.collections.ArrayList

class DivvieViewModel(application: Application) : AndroidViewModel(application) {
    // TODO organize this plz

    private var tempItem = MutableLiveData<Item>()
    val tempItemObservable: LiveData<Item>
        get() = tempItem

    fun getTempItem(): Item? {
        return tempItem.value
    }

    fun setTempItem(item: Item) {
        tempItem.value = item
    }

    fun commitItem() {
        val temp = tempItem.value!!
        temp.finalSplitPrice = temp.tempSplitPrice
        temp.tempSplitPrice = AMOUNT_DEFAULT
        itemStack.push(temp)
        tempItem.value = Item()
        for (person in getAllPersonStatic()) {
            person.subtotal += person.tempPrice
            person.tempPrice = AMOUNT_DEFAULT
            updatePerson(person)
        }
    }

    private val itemStack: Stack<Item> = Stack()


    fun resetTempItem() {
        val temp = tempItem.value ?: Item()
        temp!!.listOfIndex.clear()
        tempItem.value = temp
    }

    fun alterTempItem(i: Int) {
        val temp = tempItem.value
        val list = temp!!.listOfIndex
        if (list.contains(i)) {
            list.remove(i)
        } else {
            list.add(i)
        }
        val basePrice = temp.basePrice
        temp.tempSplitPrice = basePrice.div(list.size)
        tempItem.value = temp
    }

    private val dao = DivvieDatabase.getInstance(application).dao()

    private fun getAllPersonStatic() = dao.getAllPersonStatic()

    fun getAllPerson() = dao.getAllPerson()

    private fun findPerson(id: Int) = dao.findPerson(id)

    fun insertPerson(person: Person) { dao.insertPerson(person) }

    fun deletePerson(person: Person) = dao.deletePerson(person)

    fun getNumberOfPeople() = dao.getNumberOfPeople()

    fun getNumberOfPeopleStatic() = dao.getNumberOfPeopleStatic()

    private fun updatePerson(person: Person) {dao.updatePerson(person)}

    fun splitPretaxEqually() {
        for (person in getAllPersonStatic()) {
            person.subtotal = getSubtotal()?.div(getAllPersonStatic().size) ?: AMOUNT_DEFAULT
            person.tax = AMOUNT_DEFAULT
            person.tip = AMOUNT_DEFAULT
            person.tempPrice = AMOUNT_DEFAULT
            updatePerson(person)
        }
    }

    fun split(index: Int) {
        val item = tempItem.value
        if (item != null) {
            val person = findPerson(index)
            if (item.listOfIndex.contains(index)) {
                person.tempPrice = item.tempSplitPrice
            } else {
                person.tempPrice = AMOUNT_DEFAULT
            }
            updatePerson(person)
        }
    }

    fun clearPersonalData() {
        for (person in getAllPersonStatic()) {
            person.subtotal = AMOUNT_DEFAULT
            person.tax = AMOUNT_DEFAULT
            person.tip = AMOUNT_DEFAULT
            person.tempPrice = AMOUNT_DEFAULT
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

    private val displayPrices = MutableLiveData<Boolean>()
    val displayPricesObservable: LiveData<Boolean>
        get() = displayPrices

    private val selectPerson = MutableLiveData<Boolean>()
    val selectPersonObservable: LiveData<Boolean>
        get() = selectPerson

    private val subtotal = MutableLiveData<Double>()
    val subtotalObservable: LiveData<Double>
        get() = subtotal

    private val tax = MutableLiveData<Double>()

    private val tip = MutableLiveData<Double>()

    private val total = MutableLiveData<Double>()
    val totalObservable: LiveData<Double>
        get() = total

    fun setTotal() {
        val subtotal: Double = getSubtotal() ?: AMOUNT_DEFAULT
        val tax: Double = getTax() ?: AMOUNT_DEFAULT
        val tip: Double = getTip() ?: AMOUNT_DEFAULT
        total.value = subtotal + tax + tip
    }

    fun setDisplayPrices(bool: Boolean) {
        displayPrices.value = bool
    }

    fun setSelectPerson(bool: Boolean) {
        selectPerson.value = bool
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
    }

    private fun getTip(): Double? {
        return tip.value
    }
}

