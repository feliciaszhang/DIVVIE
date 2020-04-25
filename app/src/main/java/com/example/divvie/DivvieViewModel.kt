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

class DivvieViewModel(application: Application) : AndroidViewModel(application) {

    fun onEvent(event: DivvieViewEvent) {
        when (event) {
            is InputViewEvent.DisplayFragment -> onDisplayInputFragment()
            is InputViewEvent.InsertPerson -> onInsertPerson()
            is InputViewEvent.RemovePerson -> onRemovePerson()
            is InputViewEvent.EnterSubtotal -> onEnterSubtotal(event.input)
            is InputViewEvent.EnterTax -> onEnterTax(event.input)
            is InputViewEvent.Next -> onInputNext()

            is SplitViewEvent.DisplayFragment -> onDisplaySplitFragment()
            is SplitViewEvent.SplitEqually -> onSplitEqually()
            is SplitViewEvent.EnterIndividually -> onEnterIndividually()
            is SplitViewEvent.Back -> onSplitBack()

            is ResultViewEvent.DisplayFragment -> onDisplayResultFragment()
            is ResultViewEvent.EnterTip -> onEnterTip(event.input)
            //TODO is ResultViewEvent.ToggleFormat ->
            is ResultViewEvent.Back -> onSplitBack()
            is ResultViewEvent.StartOver -> onStartOver()

            is ItemViewEvent.DisplayFragment -> onDisplayItemFragment()
            is ItemViewEvent.EnterItemPrice -> onEnterItemPrice(event.input)
            is ItemViewEvent.Back -> onItemBack()
            is ItemViewEvent.Next -> onItemNext()
            is ItemViewEvent.ClearAll -> onClearAll()
            is ItemViewEvent.Done -> onDone()

            is BowlsViewEvent.DisplayFragment -> onDisplayBowlFragment()
        }
    }

    private fun onDisplayBowlFragment() {
        setDisplayPrices(false)
    }

    private fun onDisplayInputFragment() {
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

    private fun onDisplayItemFragment() {
        setSelectPerson(false)
        setTempItem(Item())
    }

    private fun onEnterItemPrice(input: String) {
        val temp = tempItem.value
        if (input != "") {
            temp!!.basePrice = input.toDouble()
            setTempItem(temp)
        } else {
            temp!!.basePrice = AMOUNT_DEFAULT
            setTempItem(temp)
        }
    }

    private fun onItemNext() {
        setSelectPerson(true)
    }

    private fun onDone() {
        setSelectPerson(false)
        commitItem()
    }

    private fun onItemBack() {}

    private fun onClearAll() {}
    ////////////////////////////////////////////////////


    private fun commitItem() {
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

    private fun splitPretaxEqually() {
        for (person in getAllPersonStatic()) {
            person.subtotal = getSubtotal()?.div(getAllPersonStatic().size) ?: AMOUNT_DEFAULT
            person.tax = AMOUNT_DEFAULT
            person.tip = AMOUNT_DEFAULT
            person.tempPrice = AMOUNT_DEFAULT
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
    private fun setDisplayPrices(bool: Boolean) {
        displayPrices.value = bool
    }

    private val selectPerson = MutableLiveData<Boolean>()
    val selectPersonObservable: LiveData<Boolean>
        get() = selectPerson
    private fun setSelectPerson(bool: Boolean) {
        selectPerson.value = bool
    }

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
    private fun setTip(num: Double) {
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

    private var leftover = MutableLiveData<Double>()
    val leftoverObservable: LiveData<Double>
        get() = leftover
    private fun setLeftover(num: Double) {
        leftover.value = num
    }

    private var tempItem = MutableLiveData<Item>()
    val tempItemObservable: LiveData<Item>
        get() = tempItem
    private fun setTempItem(item: Item) {
        tempItem.value = item
    }

    private val dao = DivvieDatabase.getInstance(application).dao()

    private fun getAllPersonStatic() = dao.getAllPersonStatic()

    fun getAllPerson() = dao.getAllPerson()

    private fun findPerson(id: Int) = dao.findPerson(id)

    private fun insertPerson(person: Person) { dao.insertPerson(person) }

    private fun deletePerson(person: Person) = dao.deletePerson(person)

    fun getNumberOfPeople() = dao.getNumberOfPeople()

    fun getNumberOfPeopleStatic() = dao.getNumberOfPeopleStatic()

    private fun updatePerson(person: Person) {dao.updatePerson(person)}


    fun getSubtotal(): Double? {
        return subtotal.value
    }


    fun getTax(): Double? {
        return tax.value
    }


    private fun getTip(): Double? {
        return tip.value
    }

    fun getLeftover(): Double? {
        return leftover.value
    }

    fun getTempItem(): Item? {
        return tempItem.value
    }
}

