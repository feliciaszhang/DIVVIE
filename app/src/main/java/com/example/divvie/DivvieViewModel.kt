package com.example.divvie

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.divvie.data.DivvieDatabase
import com.example.divvie.data.Item
import com.example.divvie.data.Person
import java.util.*

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
            is ResultViewEvent.ToggleFormat -> onToggleFormat()
            is ResultViewEvent.Back -> onResultBack()
            is ResultViewEvent.StartOver -> onStartOver()
        }
    }

    fun onEvent(event: ItemViewEvent) {
        when (event) {
            is ItemViewEvent.DisplayFragment -> onDisplayItemFragment()
            is ItemViewEvent.EnterItemPrice -> onEnterItemPrice(event.input)
            is ItemViewEvent.Back -> onItemBack()
            is ItemViewEvent.Next -> onItemNext()
            is ItemViewEvent.ClearAll -> onClearAll()
            is ItemViewEvent.Done -> onDone()
        }
    }

    fun onEvent(event: BowlsViewEvent) {
        when (event) {
            is BowlsViewEvent.DisplayFragment -> onDisplayBowlFragment()
            is BowlsViewEvent.ClickBowl -> onClickBowl(event.index)
           // is BowlsViewEvent.DisplayBreakdown -> onDisplayBreakdown()
        }
    }

    private fun onDisplayBowlFragment() {
        setSelectPerson(false)
    }

    private fun onClickBowl(index: Int) {
        alterTempItem(index)
    }

    private fun onDisplayInputFragment() {
        for (i in 0 until NUMBER_OF_PEOPLE_DEFAULT) {
            insertPerson(Person(id = i))
        }
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
    }

    private fun onDisplaySplitFragment() {
        splitPretaxEqually()
    }

    private fun onSplitEqually() {}

    private fun onEnterIndividually() {
        clearPersonalData()
    }

    private fun onSplitBack() {}

    private fun onDisplayResultFragment() {
        setTip(0.0)
        calculatePersonResult()
    }

    private fun onEnterTip(input: String) {
        if (input != "") {
            setTip(input.toDouble())
        } else {
            setTip(0.0)
        }
        calculatePersonResult()
    }

    private fun onToggleFormat() {}

    private fun onResultBack() {}

    private fun onStartOver() {}

    private fun onDisplayItemFragment() {
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

    private fun onItemBack() {}

    private fun onClearAll() {}
    ////////////////////////////////////////////////////


    private fun commitItem() {
        val item = tempItem.value
        item!!.finalSplitPrice = item.tempSplitPrice
        item.tempSplitPrice = 0.0
        itemStack.push(item)
        val prevLeftover = leftover.value
        setLeftover(prevLeftover!! - item.basePrice)
        tempItem.value = Item()
        for (person in getAllPersonStatic()) {
            val temp = person.personalTempPrice ?: 0.0
            person.personalSubtotal = person.personalSubtotal!! + temp
            person.personalTempPrice = null
            updatePerson(person)
        }
    }

    private val itemStack: Stack<Item> = Stack()

    fun alterTempItem(index: Int) {
        val item = tempItem.value
        val list = item!!.listOfIndex
        if (list.contains(index)) {
            list.remove(index)
        } else {
            list.add(index)
        }
        val basePrice = item.basePrice
        item.tempSplitPrice = basePrice / list.size
        tempItem.value = item
        split(item, index)
    }

    fun split(item: Item, index: Int) {
        val person = findPerson(index)
        if (item.listOfIndex.contains(index)) {
            person.personalTempPrice = item.tempSplitPrice
        } else {
            person.personalTempPrice = null
        }
        updatePerson(person)
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

    private fun splitPretaxEqually() {
        for (person in getAllPersonStatic()) {
            person.personalSubtotal = getSubtotal()!! / getAllPersonStatic().size
            person.personalTax = 0.0
            person.personalTip = 0.0
            person.personalTempPrice = 0.0
            updatePerson(person)
        }
    }

    private fun clearPersonalData() {
        for (person in getAllPersonStatic()) {
            person.personalSubtotal = 0.0
            person.personalTax = 0.0
            person.personalTip = 0.0
            person.personalTempPrice = 0.0
            updatePerson(person)
        }
    }

    private fun calculatePersonResult() {
        for (person in getAllPersonStatic()) {
            val tax: Double = getTax()!!
            val tip: Double = getTip()!!
            val ratio = person.personalSubtotal!! / getSubtotal()!!
            person.personalTax = ratio * tax
            person.personalTip = ratio * tip
            updatePerson(person)
        }
    }

    private val selectPerson = MutableLiveData<Boolean>()
    val selectPersonObservable: LiveData<Boolean>
        get() = selectPerson
    private fun setSelectPerson(bool: Boolean) {
        selectPerson.value = bool
    }
    fun getSelectPerson(): Boolean? {
        return selectPerson.value
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

    private var tempItem = MutableLiveData<Item>()
    val tempItemObservable: LiveData<Item>
        get() = tempItem
    private fun setTempItem(item: Item) {
        tempItem.value = item
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




    fun getLeftover(): Double? {
        return leftover.value
    }



    fun getTempItem(): Item? {
        return tempItem.value
    }
}

