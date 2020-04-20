package com.example.divvie

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.divvie.database.DivvieDatabase
import com.example.divvie.database.Item
import com.example.divvie.database.Person
import java.util.*
import kotlin.collections.ArrayList

class DivvieViewModel(application: Application) : AndroidViewModel(application) {
    private var currentItem: Item? = null

    fun setCurrentItem(item: Item) {
        currentItem = item
    }

    private val itemStack: Stack<Item> = Stack()

    private val listOfSelected = MutableLiveData<ArrayList<Int>>()
    val listOfSelectedObservable: LiveData<ArrayList<Int>>
        get() = listOfSelected

    fun resetListOfSelected() {
        val list = listOfSelected.value ?: ArrayList()
        list.clear()
        listOfSelected.value = list
    }

    fun alterListOfSelected(i: Int) {
        var list = listOfSelected.value
        if (list != null && list.contains(i)) {
            list.remove(i)
        } else if (list == null) {
            list = ArrayList(i)
        } else {
            list.add(i)
        }
        listOfSelected.value = list
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
            updatePerson(person)
        }
    }

//    fun split(index: Int) {
//        val currentPrice = getCurrentItemPrice()
//        val item = currentItem
//        if (item != null) {
//            val selectedPerson = findPerson(index)
//            item.listOfIndex.add(index)
//            val splitBetween = item.listOfIndex.size
//            item.splitPrice = currentPrice?.div(splitBetween) ?: currentPrice
//            itemStack.push(item)
//            selectedPerson.subtotal += item.splitPrice!!
//            updatePerson(selectedPerson)
//        }
//    }

    fun split(index: Int) {
        val currentPrice = getCurrentItemPrice()
        val item = currentItem
        val list = listOfSelected.value
        if (list != null && list.contains(index)) {
            if (item != null) {
                val splitBetween = list.size
                item.splitPrice = currentPrice?.div(splitBetween) ?: currentPrice
                item.listOfIndex = list
                itemStack.push(item)

            }
        }
        if (item != null) {
            val selectedPerson = findPerson(index)
            item.listOfIndex.add(index)
            val splitBetween = item.listOfIndex.size
            item.splitPrice = currentPrice?.div(splitBetween) ?: currentPrice
            itemStack.push(item)
            selectedPerson.subtotal += item.splitPrice!!
            updatePerson(selectedPerson)
            // TODO update all people in listOfIndex
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

    fun setCurrentItemPrice(num: Double) {
        currentItemPrice.value = num
    }

    fun getCurrentItemPrice(): Double? {
        return currentItemPrice.value
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

