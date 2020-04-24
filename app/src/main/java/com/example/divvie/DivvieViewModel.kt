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

    fun onEvent(event: InputViewEvent) {
        when (event) {
            is InputViewEvent.DisplayFragment -> onDisplayInputFragment()
            is InputViewEvent.InsertPerson -> onInsertPerson()
            is InputViewEvent.RemovePerson -> onRemovePerson()
            is InputViewEvent.EnterSubtotal -> onEnterSubtotal(event.input)
            is InputViewEvent.EnterTax -> onEnterTax(event.input)
            is InputViewEvent.Next -> onInputNext()
        }
    }

    fun onEvent(event: SplitViewEvent) {
        when (event) {
            is SplitViewEvent.DisplayFragment -> onDisplaySplitFragment()
            is SplitViewEvent.SplitEqually -> onSplitEqually()
            is SplitViewEvent.EnterIndividually -> onEnterIndividually()
            is SplitViewEvent.Back -> onSplitBack()
        }
    }

    fun onEvent(event: ResultViewEvent) {
        when (event) {
            is ResultViewEvent.DisplayFragment -> onDisplayResultFragment()
            is ResultViewEvent.EnterTip -> onEnterTip(event.input)
            //TODO is ResultViewEvent.ToggleFormat ->
            is ResultViewEvent.Back -> onSplitBack()
            is ResultViewEvent.StartOver -> onStartOver()
        }
    }

    private fun onDisplayInputFragment() {
        setDisplayPrices(false)
        for (i in 0 until NUMBER_OF_PEOPLE_DEFAULT) {
            insertPerson(Person(id = i))
        }
        setSubtotal(AMOUNT_DEFAULT)
        setTax(AMOUNT_DEFAULT)
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

    private fun onEnterSubtotal(input: String) {
        if (input != "") {
            setSubtotal(input.toDouble())
        } else {
            setSubtotal(AMOUNT_DEFAULT)
            // TODO remind user it's pretax
            // TODO show user this cannot be 0
        }
    }

    private fun onEnterTax(input: String) {
        if (input != "") {
            setTax(input.toDouble())
        } else {
            setTax(AMOUNT_DEFAULT)
        }
    }

    private fun onInputNext() {
        splitPretaxEqually()
    }

    private fun onDisplaySplitFragment() {
        setDisplayPrices(true)
    }

    private fun onSplitEqually() {}

    private fun onEnterIndividually() {
        clearPersonalData()
    }

    private fun onSplitBack() {}

    private fun onDisplayResultFragment() {
        setTip(AMOUNT_DEFAULT)
        calculatePersonResult()
    }

    private fun onEnterTip(input: String) {
        if (input != "") {
            setTip(input.toDouble())
        } else {
            setTip(AMOUNT_DEFAULT)
        }
        calculatePersonResult()
    }

    private fun onStartOver() {}

    private var leftover = MutableLiveData<Double>()
    val leftoverObservable: LiveData<Double>
        get() = leftover

    fun getLeftover(): Double? {
        return leftover.value
    }

    fun setLeftover(num: Double) {
        leftover.value = num
    }

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
        val prevLeftover = leftover.value
        setLeftover(prevLeftover!! - temp.basePrice)
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
        temp.listOfIndex.clear()
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

    private fun splitPretaxEqually() {
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

    private fun clearPersonalData() {
        for (person in getAllPersonStatic()) {
            person.subtotal = AMOUNT_DEFAULT
            person.tax = AMOUNT_DEFAULT
            person.tip = AMOUNT_DEFAULT
            person.tempPrice = AMOUNT_DEFAULT
            updatePerson(person)
        }
    }

    private fun calculatePersonResult() {
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
    private fun setSubtotal(num: Double) {
        subtotal.value = num
        setTotal()
        setLeftover(num)
    }

    private val tax = MutableLiveData<Double>()
    private fun setTax(num: Double) {
        tax.value = num
        setTotal()
    }

    private val tip = MutableLiveData<Double>()
    fun setTip(num: Double) {
        tip.value = num
        setTotal()
    }

    private val total = MutableLiveData<Double>()
    val totalObservable: LiveData<Double>
        get() = total
    private fun setTotal() {
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



    fun getSubtotal(): Double? {
        return subtotal.value
    }


    fun getTax(): Double? {
        return tax.value
    }


    private fun getTip(): Double? {
        return tip.value
    }
}

