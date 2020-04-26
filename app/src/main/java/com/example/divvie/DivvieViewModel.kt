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
            is MainEvent.DisplayActivity -> onDisplayActivity()

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
            is ResultViewEvent.EnterCurrencyTip -> onEnterCurrencyTip(event.input)
            is ResultViewEvent.EnterPercentageTip -> onEnterPercentageTip(event.input)
            is ResultViewEvent.SelectCurrency -> onSelectCurrency()
            is ResultViewEvent.SelectPercentage -> onSelectPercentage()
            is ResultViewEvent.Back -> onResultBack()
            is ResultViewEvent.StartOver -> onStartOver()

            is ItemViewEvent.DisplayFragment -> onDisplayItemFragment()
            is ItemViewEvent.EnterItemPrice -> onEnterItemPrice(event.input)
            is ItemViewEvent.Back -> onItemBack()
            is ItemViewEvent.Undo -> onUndo()
            is ItemViewEvent.Next -> onItemNext()
            is ItemViewEvent.ClearAll -> onClearAll()
            is ItemViewEvent.Done -> onDone()

            is BowlsViewEvent.DisplayFragment -> onDisplayBowlFragment()
            is BowlsViewEvent.ClickBowl -> onClickBowl(event.i)
        }
    }

    private fun onDisplayActivity() {
        deleteAllPerson()
        setSubtotal(null)
        for (i in 0 until NUMBER_OF_PEOPLE_DEFAULT) {
            insertPerson(Person(i, null, null, null, null))
        }
    }

    private fun onDisplayBowlFragment() {}

    private fun onClickBowl(i: Int) {
        alterTempItem(i)
    }

    private fun onDisplayInputFragment() {}

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
            setSubtotal(0.0)
            // TODO remind user it's pretax
            // TODO show user this cannot be 0
        }
    }

    private fun onEnterTax(input: String) {
        if (input != "") {
            setTax(input.toDouble())
        } else {
            setTax(0.0)
        }
    }

    private fun onInputNext() {
        if (getTax() == null) {
            setTax(0.0)
        }
        splitPretaxEqually() // in case where ItemFragment navigate to SplitFragment and it's not equal
    }

    private fun onDisplaySplitFragment() {
        setSelectPerson(false) // in case where ItemFragment navigate to SplitFragment when selectPerson is true
    }

    private fun onSplitEqually() {}

    private fun onEnterIndividually() {
        clearPersonalData()
    }

    private fun onSplitBack() {
        nullifyPersonalData()
    }

    private fun onDisplayResultFragment() {
        setTip(0.0)
        setIsCurrency(true)
        calculatePersonResult()
    }

    private fun onEnterCurrencyTip(input: String) {
        if (input != "") {
            setTip(input.toDouble())
        } else {
            setTip(0.0)
        }
        calculatePersonResult()
    }

    private fun onEnterPercentageTip(input: String) {
        if (input != "") {
            val subtotal = getSubtotal()!!
            setTip(input.toDouble() * subtotal)
        } else {
            setTip(0.0)
        }
        calculatePersonResult()
    }

    private fun onSelectCurrency() {
        setIsCurrency(true)
    }

    private fun onSelectPercentage() {
        setIsCurrency(false)
    }

    private fun onResultBack() {
        splitPretaxEqually()
        setLeftover(getSubtotal()!!)
    }

    private fun onStartOver() {
        deleteAllPerson()
    }

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
            temp!!.basePrice = 0.0
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

    private fun onUndo() {
        if (selectPerson.value!!) {
            setSelectPerson(false)
            removeTempItem()
        } else {
            removeFromStack()
        }
    }

    private fun onItemBack() {
        tempItem.value = Item()
        itemStack.value = Stack()
        splitPretaxEqually()
    }

    private fun onClearAll() {
        //TODO clearAll
    }

    private fun removeTempItem() {
        val removedItem = tempItem.value!!
        for (index in removedItem.listOfIndex) {
            val person = findPerson(index)
            person.tempPrice = 0.0
            updatePerson(person)
        }
        tempItem.value = Item()
    }

    private fun removeFromStack() {
        val stack = itemStack.value!!
        val removedItem = stack.pop()
        val prevLeftover = leftover.value
        setLeftover(prevLeftover!! + removedItem.basePrice)
        for (index in removedItem.listOfIndex) {
            val person = findPerson(index)
            person.subtotal = person.subtotal!! - removedItem.finalSplitPrice
            updatePerson(person)
        }
        itemStack.value = stack
    }

    private fun commitItem() {
        val pushedItem = tempItem.value!!
        pushedItem.finalSplitPrice = pushedItem.tempSplitPrice
        pushedItem.tempSplitPrice = 0.0
        pushToStack(pushedItem)
        val prevLeftover = leftover.value
        setLeftover(prevLeftover!! - pushedItem.basePrice)
        tempItem.value = Item()
        for (person in getAllPersonStatic()) {
            val personalTemp = person.tempPrice ?: 0.0
            person.subtotal = person.subtotal!! + personalTemp
            person.tempPrice = 0.0
            updatePerson(person)
        }
    }

    private fun alterTempItem(i: Int) {
        val alteredItem = tempItem.value
        val listOfIndex = alteredItem!!.listOfIndex
        if (listOfIndex.contains(i)) {
            listOfIndex.remove(i)
        } else {
            listOfIndex.add(i)
        }
        val basePrice = alteredItem.basePrice
        alteredItem.tempSplitPrice = basePrice / listOfIndex.size
        tempItem.value = alteredItem
        for (person in getAllPersonStatic()) {
            if (person.id in alteredItem.listOfIndex) {
                person.tempPrice = alteredItem.tempSplitPrice
                updatePerson(person)
            } else {
                person.tempPrice = 0.0
                updatePerson(person)
            }
        }
    }

    private fun splitPretaxEqually() {
        for (person in getAllPersonStatic()) {
            person.subtotal = getSubtotal()!! / getAllPersonStatic().size
            person.tax = 0.0
            person.tip = 0.0
            person.tempPrice = 0.0
            updatePerson(person)
        }
    }

    private fun clearPersonalData() {
        for (person in getAllPersonStatic()) {
            person.subtotal = 0.0
            person.tax = 0.0
            person.tip = 0.0
            person.tempPrice = 0.0
            updatePerson(person)
        }
    }

    private fun nullifyPersonalData() {
        for (person in getAllPersonStatic()) {
            person.subtotal = null
            person.tax = null
            person.tip = null
            person.tempPrice = null
            updatePerson(person)
        }
    }

    private fun calculatePersonResult() {
        for (person in getAllPersonStatic()) {
            val tax: Double = getTax()!!
            val tip: Double = getTip()!!
            val ratio = person.subtotal!! / getSubtotal()!!
            person.tax = ratio * tax
            person.tip = ratio * tip
            updatePerson(person)
        }
    }

    private val selectPerson = MutableLiveData<Boolean>()
    val selectPersonObservable: LiveData<Boolean>
        get() = selectPerson
    private fun setSelectPerson(bool: Boolean) {
        selectPerson.value = bool
    }

    private val subtotal = MutableLiveData<Double?>()
    val subtotalObservable: LiveData<Double?>
        get() = subtotal
    private fun setSubtotal(num: Double?) {
        subtotal.value = num
        if (num != null) {
            setTotal()
            setLeftover(num)
        }
    }
    fun getSubtotal(): Double? {
        return subtotal.value
    }

    private val tax = MutableLiveData<Double>()
    private fun setTax(num: Double) {
        tax.value = num
        setTotal()
    }
    fun getTax(): Double? {
        return tax.value
    }

    private val tip = MutableLiveData<Double>()
    private fun setTip(num: Double) {
        tip.value = num
        setTotal()
    }
    fun getTip(): Double? {
        return tip.value
    }

    private val total = MutableLiveData<Double>()
    val totalObservable: LiveData<Double>
        get() = total
    private fun setTotal() {
        val subtotal: Double = getSubtotal() ?: 0.0
        val tax: Double = getTax() ?: 0.0
        val tip: Double = getTip() ?: 0.0
        total.value = subtotal + tax + tip
    }

    private var leftover = MutableLiveData<Double>()
    val leftoverObservable: LiveData<Double>
        get() = leftover
    private fun setLeftover(num: Double) {
        leftover.value = num
    }
    fun getLeftover(): Double? {
        return leftover.value
    }

    private var tempItem = MutableLiveData<Item>()
    val tempItemObservable: LiveData<Item>
        get() = tempItem
    private fun setTempItem(item: Item) {
        tempItem.value = item
    }
    fun getTempItem(): Item? {
        return tempItem.value
    }

    private val itemStack = MutableLiveData<Stack<Item>>()
    val itemStackObservable: LiveData<Stack<Item>>
        get() = itemStack
    private fun pushToStack(item: Item) {
        var stack = itemStack.value
        if (stack == null) {
            stack = Stack()
        }
        stack.push(item)
        itemStack.value = stack
    }

    private val isCurrency = MutableLiveData<Boolean>()
    val isCurrencyObservable: LiveData<Boolean>
        get() = isCurrency
    private fun setIsCurrency(bool: Boolean) {
        isCurrency.value = bool
    }

    private val dao = DivvieDatabase.getInstance(application).dao()

    private fun getAllPersonStatic() = dao.getAllPersonStatic()

    private fun findPerson(id: Int) = dao.findPerson(id)

    private fun insertPerson(person: Person) { dao.insertPerson(person) }

    private fun deletePerson(person: Person) = dao.deletePerson(person)

    private fun updatePerson(person: Person) {dao.updatePerson(person)}

    private fun deleteAllPerson() {dao.deleteAllPerson()}

    private fun getNumberOfPeopleStatic() = dao.getNumberOfPeopleStatic()

    fun getNumberOfPeople() = dao.getNumberOfPeople()

    fun getAllPerson() = dao.getAllPerson()
}

