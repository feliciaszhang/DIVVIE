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

    private val viewState = MutableLiveData<DivvieViewState>()
    val viewStateObservable: LiveData<DivvieViewState>
        get() = viewState

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
        viewState.value = DivvieViewState()
        for (person in viewState.value!!.personList) {
            insertPerson(person)
        }
    }

    private fun onDisplayBowlFragment() {}

    private fun onClickBowl(i: Int) {
        alterTempItem(i)
    }

    private fun onDisplayInputFragment() {}

    private fun onInsertPerson() {
        val num = viewState.value!!.personList.size
        if (num < MAX_NUMBER_OF_PEOPLE) {
            insertPerson(Person(id = num))
        }
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onRemovePerson() {
        val num = viewState.value!!.personList.size
        if (num > MIN_NUMBER_OF_PEOPLE) {
            deletePerson(Person(id = num - 1))
        }
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onEnterSubtotal(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(
                subtotal = input.toDouble(),
                isSubtotalEditing = true,
                leftover = input.toDouble()
            )
        } else {
            viewState.value = viewState.value!!.copy(
                subtotal = 0.0,
                isSubtotalEditing = true,
                leftover = 0.0
            )
            // TODO remind user it's pretax
            // TODO show user this cannot be 0
        }
    }

    private fun onEnterTax(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(
                tax = input.toDouble(),
                isTaxEditing = true
            )
        } else {
            viewState.value = viewState.value!!.copy(
                tax = 0.0,
                isTaxEditing = true
            )
        }
    }

    private fun onInputNext() {
        if (viewState.value!!.tax == null) {
            viewState.value = viewState.value!!.copy(
                tax = 0.0
            )
        }
        splitPretaxEqually()
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
        // in case where ItemFragment navigate to SplitFragment and it's not equal
    }

    private fun onDisplaySplitFragment() {
        viewState.value = viewState.value!!.copy(
            isClickableBowls = false
        )
        // in case where ItemFragment navigate to SplitFragment when selectPerson is true
    }

    private fun onSplitEqually() {}

    private fun onEnterIndividually() {
        clearPersonalData()
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onSplitBack() {
        nullifyPersonalData()
        viewState.value = viewState.value!!.copy(
            isSubtotalEditing = false,
            isTaxEditing = false,
            personList = getAllPersonStatic()
        )
    }

    private fun onDisplayResultFragment() {
        calculatePersonResult()
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onEnterCurrencyTip(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(
                tip = input.toDouble(),
                isTipEditing = true
            )
        } else {
            viewState.value = viewState.value!!.copy(
                tip = 0.0,
                isTipEditing = true
            )
        }
        calculatePersonResult()
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onEnterPercentageTip(input: String) {
        if (input != "") {
            val subtotal = viewState.value!!.subtotal!!
            viewState.value = viewState.value!!.copy(
                tip = input.toDouble() / 100 * subtotal,
                isTipEditing = true
            )
        } else {
            viewState.value = viewState.value!!.copy(
                tip = 0.0,
                isTipEditing = true
            )
        }
        calculatePersonResult()
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onSelectCurrency() {
        viewState.value = viewState.value!!.copy(
            isCurrencyTip = true,
            isTipEditing = false
        )
    }

    private fun onSelectPercentage() {
        viewState.value = viewState.value!!.copy(
            isCurrencyTip = false,
            isTipEditing = false
        )
    }

    private fun onResultBack() {
        // TODO split equally?
        splitPretaxEqually()
        val subtotal = viewState.value!!.subtotal
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic(),
            tip = null,
            leftover = subtotal
        )
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
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.subtotal = vs.subtotal!! / vs.personList.size
            person.tax = 0.0
            person.tip = 0.0
            person.tempPrice = 0.0
            updatePerson(person)
        }
    }

    private fun clearPersonalData() {
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.subtotal = 0.0
            person.tax = 0.0
            person.tip = 0.0
            person.tempPrice = 0.0
            updatePerson(person)
        }
    }

    private fun nullifyPersonalData() {
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.subtotal = null
            person.tax = null
            person.tip = null
            person.tempPrice = null
            updatePerson(person)
        }
    }

    private fun calculatePersonResult() {
        val vs = viewState.value!!
        for (person in vs.personList) {
            val tax = vs.tax!!
            val tip = vs.tip ?: 0.0
            val ratio = person.subtotal!! / vs.subtotal!!
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

    private val dao = DivvieDatabase.getInstance(application).dao()

    private fun getAllPersonStatic() = dao.getAllPersonStatic()

    private fun findPerson(id: Int) = dao.findPerson(id)

    private fun insertPerson(person: Person) { dao.insertPerson(person) }

    private fun deletePerson(person: Person) = dao.deletePerson(person)

    private fun updatePerson(person: Person) {dao.updatePerson(person)}

    private fun deleteAllPerson() {dao.deleteAllPerson()}

    fun getAllPerson() = dao.getAllPerson()
}

