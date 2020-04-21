package com.example.divvie

import android.app.Application
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

    private var finalItem: Item? = null

    fun setFinalItem(item: Item) {
        finalItem = item
    }

    fun commitItem() {
        itemStack.push(finalItem!!)
        for (person in getAllPersonStatic()) {
            person.subtotal += person.tempPrice
            person.tempPrice = AMOUNT_DEFAULT
            updatePerson(person)
        }
    }

    private val itemStack: Stack<Item> = Stack()

    private val itemMap = MutableLiveData<ArrayList<Int>>()
    val itemMapObservable: LiveData<ArrayList<Int>>
        get() = itemMap

    fun resetItemMap() {
        val map = itemMap.value ?: ArrayList()
        map.clear()
        itemMap.value = map
    }

    fun alterItemMap(i: Int) {
        var map = itemMap.value
        if (map != null && map.contains(i)) {
            map.remove(i)
        } else if (map == null) {
            map = ArrayList(i)
        } else {
            map.add(i)
        }
        itemMap.value = map
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
        // TODO make local val so we don't calc this every time
        val basePrice = getTempItemPrice()
        val item = finalItem
        val splitPrice = basePrice!!.div(itemMap.value!!.size)
        if (item != null) {
            item.basePrice = basePrice
            item.splitPrice = splitPrice
            val person = findPerson(index)
            if (itemMap.value!!.contains(index)) {
                person.tempPrice = splitPrice
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

    private val tempItemPrice = MutableLiveData<Double>()
    val tempItemPriceObservable: LiveData<Double>
        get() = tempItemPrice

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

    fun setTempItemPrice(num: Double) {
        tempItemPrice.value = num
    }

    fun getTempItemPrice(): Double? {
        return tempItemPrice.value
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

