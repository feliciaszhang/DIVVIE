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
            is SplitViewEvent.Calculate -> onCalculate()
            is SplitViewEvent.BackToInput -> onSplitBackToInput()
            is SplitViewEvent.BackToEqual -> onSplitBackToEqual()

            is ResultViewEvent.DisplayFragment -> onDisplayResultFragment()
            is ResultViewEvent.EnterCurrencyTip -> onEnterCurrencyTip(event.input)
            is ResultViewEvent.EnterPercentageTip -> onEnterPercentageTip(event.input)
            is ResultViewEvent.SelectCurrency -> onSelectCurrency()
            is ResultViewEvent.SelectPercentage -> onSelectPercentage()
            is ResultViewEvent.Back -> onResultBack()
            is ResultViewEvent.Restart -> onRestart()

            is ItemViewEvent.DisplayFragment -> onDisplayItemFragment()
            is ItemViewEvent.EnterItemPrice -> onEnterItemPrice(event.input)
            is ItemViewEvent.Back -> onItemBack()
            is ItemViewEvent.Undo -> onUndo()
            is ItemViewEvent.Next -> onItemNext()
            is ItemViewEvent.ClearAll -> onClearAll()
            is ItemViewEvent.Done -> onDone()

            is BowlsViewEvent.DisplayFragment -> onDisplayBowlFragment()
            is BowlsViewEvent.EnterName -> onEnterName(event.i, event.input)
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

    private fun onEnterName(i: Int, input: String) {
        if (i < getAllPersonStatic().size) {
            val person = findPerson(i)
            person.name = input
            updatePerson(person)
            viewState.value = viewState.value!!.copy(
                personList = getAllPersonStatic()
            )
        }
    }

    private fun onClickBowl(i: Int) {
        val vs = viewState.value!!
        val listOfIndex = vs.tempItemListOfIndex
        if (listOfIndex.contains(i)) {
            listOfIndex.remove(i)
        } else {
            listOfIndex.add(i)
        }
        val basePrice = vs.tempItemBasePrice
        val tempSplitPrice = basePrice / listOfIndex.size
        for (person in vs.personList) {
            if (person.id in listOfIndex) {
                person.tempPrice = tempSplitPrice
            } else {
                person.tempPrice = 0.0
            }
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            tempItemTempSplitPrice = tempSplitPrice,
            tempItemListOfIndex = listOfIndex,
            personList = getAllPersonStatic()
        )
    }

    private fun onDisplayInputFragment() {
        viewState.value = viewState.value!!.copy(
            editableName = true
        )
    }

    private fun onInsertPerson() {
        val num = viewState.value!!.personList.size
        if (num < MAX_GUESTS) {
            insertPerson(Person(id = num))
        }
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onRemovePerson() {
        val num = viewState.value!!.personList.size
        if (num > MIN_GUESTS) {
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
            editableName = false,
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

    private fun onCalculate() {}

    private fun onSplitBackToInput() {
        nullifyPersonalData()
        viewState.value = viewState.value!!.copy(
            isSubtotalEditing = false,
            isTaxEditing = false,
            personList = getAllPersonStatic()
        )
    }

    private fun onSplitBackToEqual() {
        splitPretaxEqually()
        val vs = viewState.value!!
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic(),
            leftover = vs.subtotal,
            itemStack = Stack()
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
        revertPersonalResult()
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic(),
            tip = null
        )
    }

    private fun onRestart() {
        nullifyPersonalData()
        val savedPersonData = getAllPersonStatic()
        viewState.value = DivvieViewState()
        viewState.value = viewState.value!!.copy(
            personList = savedPersonData
        )
    }

    private fun onDisplayItemFragment() {
        viewState.value = viewState.value!!.copy(
            isClickableBowls = false
        )
    }

    private fun onEnterItemPrice(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(
                tempItemBasePrice = input.toDouble()
            )
        } else {
            viewState.value = viewState.value!!.copy(
                tempItemBasePrice = 0.0
            )
        }
    }

    private fun onItemNext() {
        viewState.value = viewState.value!!.copy(
            isClickableBowls = true
        )
    }

    private fun onDone() {
        val vs = viewState.value!!
        val basePrice = vs.tempItemBasePrice
        val finalSplitPrice = vs.tempItemTempSplitPrice
        val listOfIndex = vs.tempItemListOfIndex
        vs.itemStack.push(Item(basePrice, finalSplitPrice, listOfIndex))
        val leftover = vs.leftover!!
        for (person in vs.personList) {
            val personalTemp = person.tempPrice ?: 0.0
            person.subtotal = person.subtotal!! + personalTemp
            person.tempPrice = 0.0
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            tempItemBasePrice = 0.0,
            tempItemFinalSplitPrice = 0.0,
            tempItemTempSplitPrice = 0.0,
            tempItemListOfIndex = ArrayList(),
            itemStack = vs.itemStack,
            leftover = leftover - basePrice,
            isClickableBowls = false,
            personList = getAllPersonStatic()
        )
    }

    private fun onItemBack() {
        splitPretaxEqually()
        viewState.value = viewState.value!!.copy(
            tempItemBasePrice = 0.0,
            tempItemFinalSplitPrice = 0.0,
            tempItemTempSplitPrice = 0.0,
            tempItemListOfIndex = ArrayList(),
            itemStack = Stack(),
            personList = getAllPersonStatic()
        )
    }

    private fun onUndo() {
        val vs = viewState.value!!
        if (vs.isClickableBowls) {
            removeTempItemFromPerson()
            viewState.value = viewState.value!!.copy(
                tempItemBasePrice = 0.0,
                tempItemFinalSplitPrice = 0.0,
                tempItemTempSplitPrice = 0.0,
                tempItemListOfIndex = ArrayList(),
                isClickableBowls = false,
                personList = getAllPersonStatic()

            )
        } else {
            val removedItem = vs.itemStack.pop()
            val basePrice = removedItem.basePrice
            val leftover = vs.leftover!!
            removeFinalItemFromPerson(removedItem)
            viewState.value = viewState.value!!.copy(
                itemStack = vs.itemStack,
                leftover = leftover + basePrice,
                personList = getAllPersonStatic()
            )
        }
    }

    private fun onClearAll() {
        val vs = viewState.value!!
        if (vs.isClickableBowls) {
            removeTempItemFromPerson()
            viewState.value = viewState.value!!.copy(
                tempItemBasePrice = 0.0,
                tempItemFinalSplitPrice = 0.0,
                tempItemTempSplitPrice = 0.0,
                tempItemListOfIndex = ArrayList(),
                isClickableBowls = false
            )
        }
        while (vs.itemStack.size > 0) {
            val removedItem = vs.itemStack.pop()
            removeFinalItemFromPerson(removedItem)
        }
        viewState.value = viewState.value!!.copy(
            itemStack = Stack(),
            leftover = vs.subtotal,
            personList = getAllPersonStatic()
        )
    }

    private fun removeTempItemFromPerson() {
        val vs = viewState.value!!
        for (index in vs.tempItemListOfIndex) {
            val person = findPerson(index)
            person.tempPrice = 0.0
            updatePerson(person)
        }
    }

    private fun removeFinalItemFromPerson(removedItem: Item) {
        for (index in removedItem.listOfIndex) {
            val person = findPerson(index)
            person.subtotal = person.subtotal!! - removedItem.finalSplitPrice
            updatePerson(person)
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

    private fun revertPersonalResult() {
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.tax = 0.0
            person.tip = 0.0
            person.tempPrice = 0.0
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

