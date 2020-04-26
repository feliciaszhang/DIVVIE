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

    private val viewState = MutableLiveData<DivvieViewState>()
    val viewStateObservable: LiveData<DivvieViewState>
        get() = viewState

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
            is SplitViewEvent.Calculate -> onCalculate()
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

    private fun onDisplayBowlFragment() {
        for (person in DivvieViewState().personList) {
            val temp = Person(person.id, null, null, null, null)
            updatePerson(temp)
        }
        viewState.value = viewState.value!!.copy(personList = getAllPersonStatic())
    }

    private fun onClickBowl(i: Int) {
        alterTempItem(i)
    }

    private fun onDisplayInputFragment() {
        for (person in DivvieViewState().personList) {
            insertPerson(person)
        }
        viewState.value = DivvieViewState()
    }

    private fun onInsertPerson() {
        val num = viewState.value!!.personList.size
        if (num < MAX_NUMBER_OF_PEOPLE) {
            insertPerson(Person(id = num))
        }
        viewState.value = viewState.value!!.copy(personList = getAllPersonStatic())
    }

    private fun onRemovePerson() {
        val num = viewState.value!!.personList.size
        if (num > MIN_NUMBER_OF_PEOPLE) {
            deletePerson(Person(id = num - 1))
        }
        viewState.value = viewState.value!!.copy(personList = getAllPersonStatic())
    }

    private fun onEnterSubtotal(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(subtotal = input.toDouble(), leftover = input.toDouble())
        } else {
            viewState.value = viewState.value!!.copy(subtotal = 0.0, leftover = 0.0)
            // TODO remind user it's pretax
            // TODO show user this cannot be 0
        }
    }

    private fun onEnterTax(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(tax = input.toDouble())
        } else {
            viewState.value = viewState.value!!.copy(tax = 0.0)
        }
    }

    private fun onInputNext() {
        splitPretaxEqually()
        viewState.value = viewState.value!!.copy(personList = getAllPersonStatic())
        // in case where ItemFragment navigate to SplitFragment and it's not equal
    }

    private fun onDisplaySplitFragment() {
        viewState.value = viewState.value!!.copy(isClickableBowls = false)
        // in case where ItemFragment navigate to SplitFragment when selectPerson is true
    }

    private fun onSplitEqually() {
        calculatePersonResult()
        viewState.value = viewState.value!!.copy(personList = getAllPersonStatic())
    }

    private fun onEnterIndividually() {
        clearPersonalData()
        viewState.value = viewState.value!!.copy(personList = getAllPersonStatic())
    }

    private fun onCalculate() {
        calculatePersonResult()
        viewState.value = viewState.value!!.copy(personList = getAllPersonStatic())
    }

    private fun onSplitBack() {
        //TODO split back
    }

    private fun onDisplayResultFragment() {}

    private fun onEnterCurrencyTip(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(tip = input.toDouble())
        } else {
            viewState.value = viewState.value!!.copy(tip = 0.0)
        }
    }

    private fun onEnterPercentageTip(input: String) {
        val vs = viewState.value!!
        if (input != "") {
            val subtotal = vs.subtotal
            viewState.value = viewState.value!!.copy(tip = input.toDouble() * subtotal)
        } else {
            viewState.value = viewState.value!!.copy(tip = 0.0)
        }
    }

    private fun onSelectCurrency() {
        viewState.value = viewState.value!!.copy(isCurrencyTip = true)
    }

    private fun onSelectPercentage() {
        viewState.value = viewState.value!!.copy(isCurrencyTip = false)
    }

    private fun onResultBack() {
        splitPretaxEqually()
        val vs = viewState.value!!
        viewState.value = viewState.value!!.copy(leftover = vs.subtotal)
    }

    private fun onStartOver() {
        deleteAllPerson()
        viewState.value = viewState.value!!.copy(personList = getAllPersonStatic())
    }

    private fun onDisplayItemFragment() {
        viewState.value = viewState.value!!.copy(isClickableBowls = false)
    }

    private fun onEnterItemPrice(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(tempItemBasePrice = input.toDouble())
        } else {
            viewState.value = viewState.value!!.copy(tempItemBasePrice = 0.0)
        }
    }

    private fun onItemNext() {
        viewState.value = viewState.value!!.copy(isClickableBowls = true)
    }

    private fun onDone() {
        commitItem()
        viewState.value = viewState.value!!.copy(isClickableBowls = false)
    }

    private fun onUndo() {
        if (viewState.value!!.isClickableBowls) {
            viewState.value = viewState.value!!.copy(isClickableBowls = true)
            removeTempItem()
        } else {
            removeFromStack()
        }
        viewState.value = viewState.value!!.copy(personList = getAllPersonStatic())
    }

    private fun onItemBack() {
        splitPretaxEqually()
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic(),
            tempItemBasePrice = 0.0,
            tempItemFinalSplitPrice = 0.0,
            tempItemTempSplitPrice = 0.0,
            tempItemListOfIndex = ArrayList(),
            itemStack = Stack()
        )
    }

    private fun onClearAll() {
        //TODO clearAll
    }

    private fun removeTempItem() {
        val vs = viewState.value!!
        for (index in vs.tempItemListOfIndex) {
            val person = findPerson(index)
            person.tempPrice = 0.0
            updatePerson(person)
        }
    }

    private fun removeFromStack() {
        val vs = viewState.value!!
        val removedItem = vs.itemStack.pop()
        val leftover = vs.leftover!!
        vs.leftover = leftover + removedItem.basePrice
        for (index in removedItem.listOfIndex) {
            val person = findPerson(index)
            person.subtotal = person.subtotal!! - removedItem.finalSplitPrice
            updatePerson(person)
        }
    }

    private fun commitItem() {
        val vs = viewState.value!!
        vs.tempItemFinalSplitPrice = vs.tempItemTempSplitPrice
        vs.tempItemTempSplitPrice = 0.0
        vs.itemStack.push(Item(vs.tempItemBasePrice, vs.tempItemFinalSplitPrice, vs.tempItemTempSplitPrice, vs.tempItemListOfIndex))
        val leftover = vs.leftover!!
        vs.leftover = leftover - vs.tempItemBasePrice
        for (person in vs.personList) {
            val personalTemp = person.tempPrice ?: 0.0
            person.subtotal = person.subtotal!! + personalTemp
            person.tempPrice = 0.0
            updatePerson(person)
        }
        viewState.value = vs
    }

    private fun alterTempItem(i: Int) {
        val vs = viewState.value!!
        val listOfIndex = vs.tempItemListOfIndex
        if (listOfIndex.contains(i)) {
            listOfIndex.remove(i)
        } else {
            listOfIndex.add(i)
        }
        val basePrice = vs.tempItemBasePrice
        vs.tempItemTempSplitPrice = basePrice / listOfIndex.size
        for (person in vs.personList) {
            if (person.id in listOfIndex) {
                person.tempPrice = vs.tempItemTempSplitPrice
            } else {
                person.tempPrice = 0.0
            }
            updatePerson(person)
        }
    }

    private fun splitPretaxEqually() {
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.subtotal = vs.subtotal / vs.personList.size
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

    private fun calculatePersonResult() {
        val vs = viewState.value!!
        for (person in vs.personList) {
            val ratio = person.subtotal!! / vs.subtotal
            person.tax = ratio * vs.tax
            person.tip = ratio * vs.tip
            updatePerson(person)
        }
    }

    private val dao = DivvieDatabase.getInstance(application).dao()

    private fun getAllPersonStatic() = dao.getAllPersonStatic()

    private fun findPerson(id: Int) = dao.findPerson(id)

    private fun insertPerson(person: Person) { dao.insertPerson(person) }

    private fun deletePerson(person: Person) = dao.deletePerson(person)

    private fun updatePerson(person: Person) {dao.updatePerson(person)}

    private fun deleteAllPerson() {dao.deleteAllPerson()}
}

