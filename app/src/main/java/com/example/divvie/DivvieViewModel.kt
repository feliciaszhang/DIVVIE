package com.example.divvie

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.divvie.data.DivvieDatabase
import com.example.divvie.data.Person
import com.example.divvie.data.Price
import java.math.BigDecimal
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
            is BowlsViewEvent.SplitBowl -> onClickBowl(event.i)
            is BowlsViewEvent.ViewBreakdown -> onViewBreakdown(event.i)
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

    private fun onViewBreakdown(i: Int?) {
        val current = viewState.value!!.personalBreakDownIndex
        if (i != current) {
            viewState.value = viewState.value!!.copy(
                isTipEditing = false,
                personalBreakDownIndex = i
            )
        } else {
            viewState.value = viewState.value!!.copy(
                isTipEditing = false,
                personalBreakDownIndex = null
            )
        }
    }

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
        val dividedMoney = Price.moneyDivider(basePrice, listOfIndex.size)
        val base = dividedMoney.base
        var acc = dividedMoney.acc
        for (person in vs.personList.sorted()) {
            if (person.id in listOfIndex) {
                if (acc > 0) {
                    person.tempPrice = Price(base, 0.01)
                    acc -= 1
                } else {
                    person.tempPrice = Price(base, 0.0)
                }
            } else {
                person.tempPrice = Price(0.0, 0.0)
            }
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
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
            isSplittingBowls = false
        )
        // in case where ItemFragment navigate to SplitFragment when selectPerson is true
    }

    private fun onSplitEqually() {}

    private fun onEnterIndividually() {
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.subtotal = 0.0
            person.tax = 0.0
            person.tip = 0.0
            person.listOfPrices = ArrayDeque()
            updatePerson(person)
        }
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
            itemList = ArrayDeque()
        )
    }

    private fun onDisplayResultFragment() {
        calculateResult()
        viewState.value = viewState.value!!.copy(
            isPersonalResult = true,
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
        calculateResult()
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onEnterPercentageTip(input: String) {
        if (input != "") {
            val subtotal = viewState.value!!.subtotal ?: 0.0
            val dividedMoney = Price.moneyDivider(input.toDouble() * subtotal, 100)
            viewState.value = viewState.value!!.copy(
                tip = dividedMoney.base,
                isTipEditing = true
            )
        } else {
            viewState.value = viewState.value!!.copy(
                tip = 0.0,
                isTipEditing = true
            )
        }
        calculateResult()
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
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.tax = 0.0
            person.tip = 0.0
            person.listOfPrices = ArrayDeque()
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            isPersonalResult = false,
            personalBreakDownIndex = null,
            personList = getAllPersonStatic(),
            tip = null
        )
    }

    private fun onRestart() {
        nullifyPersonalData()
        // TODO nullify
        val savedPersonData = getAllPersonStatic()
        viewState.value = DivvieViewState()
        viewState.value = viewState.value!!.copy(
            personList = savedPersonData
        )
    }

    private fun onDisplayItemFragment() {
        viewState.value = viewState.value!!.copy(
            isSplittingBowls = false,
            isItemEditing = false
        )
    }

    private fun onEnterItemPrice(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(
                tempItemBasePrice = input.toDouble(),
                isItemEditing = true
            )
        } else {
            viewState.value = viewState.value!!.copy(
                tempItemBasePrice = 0.0,
                isItemEditing = true
            )
        }
    }

    private fun onItemNext() {
        viewState.value = viewState.value!!.copy(
            isSplittingBowls = true
        )
    }

    private fun onDone() {
        val vs = viewState.value!!
        val basePrice = vs.tempItemBasePrice
        vs.itemList.add(basePrice)
        val leftover = vs.leftover!!
        for (person in vs.personList) {
            val personalTemp = person.tempPrice ?: Price(0.0, 0.0)
            person.subtotal = ((person.subtotal!!).toBigDecimal() + personalTemp.base.toBigDecimal() + personalTemp.acc.toBigDecimal()).toDouble()
            person.listOfPrices.add(personalTemp)
            person.tempPrice = Price(0.0, 0.0)
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            tempItemBasePrice = 0.0,
            tempItemListOfIndex = ArrayList(),
            itemList = vs.itemList,
            leftover = (leftover.toBigDecimal() - basePrice.toBigDecimal()).toDouble(),
            isSplittingBowls = false,
            isItemEditing = false,
            personList = getAllPersonStatic()
        )
    }

    private fun onItemBack() {
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.tempPrice = Price(0.0, 0.0)
            updatePerson(person)
        }
        splitPretaxEqually()
        viewState.value = viewState.value!!.copy(
            tempItemBasePrice = 0.0,
            tempItemListOfIndex = ArrayList(),
            itemList = ArrayDeque(),
            isItemEditing = false,
            personList = getAllPersonStatic()
        )
    }

    private fun onUndo() {
        val vs = viewState.value!!
        if (vs.isSplittingBowls) {
            for (person in vs.personList) {
                person.tempPrice = Price(0.0, 0.0)
                updatePerson(person)
            }
            viewState.value = viewState.value!!.copy(
                tempItemBasePrice = 0.0,
                tempItemListOfIndex = ArrayList(),
                isSplittingBowls = false,
                isItemEditing = false,
                personList = getAllPersonStatic()

            )
        } else {
            val removedItem = vs.itemList.removeLast()
            val leftover = vs.leftover!!
            for (person in vs.personList) {
                val lastPrice = person.listOfPrices.removeLast()
                person.subtotal = (person.subtotal!!.toBigDecimal() - lastPrice.base.toBigDecimal() - lastPrice.acc.toBigDecimal()).toDouble()
                updatePerson(person)
            }
            viewState.value = viewState.value!!.copy(
                itemList = vs.itemList,
                leftover = (leftover.toBigDecimal() + removedItem.toBigDecimal()).toDouble(),
                isItemEditing = false,
                personList = getAllPersonStatic()
            )
        }
    }

    private fun onClearAll() {
        val vs = viewState.value!!
        if (vs.isSplittingBowls) {
            for (person in vs.personList) {
                person.tempPrice = Price(0.0, 0.0)
                updatePerson(person)
            }
            viewState.value = viewState.value!!.copy(
                tempItemBasePrice = 0.0,
                tempItemListOfIndex = ArrayList(),
                isSplittingBowls = false
            )
        }
        vs.itemList.clear()
        for (person in vs.personList) {
            person.subtotal = 0.0
            person.listOfPrices.clear()
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            itemList = ArrayDeque(),
            leftover = vs.subtotal,
            isItemEditing = false,
            personList = getAllPersonStatic()
        )
    }

    private fun splitPretaxEqually() {
        val vs = viewState.value!!
        val dividedMoney = Price.moneyDivider(vs.subtotal!!, vs.personList.size)
        val base = dividedMoney.base
        var acc = dividedMoney.acc
        for (person in vs.personList) {
            person.listOfPrices = ArrayDeque()
            if (acc > 0) {
                person.subtotal = (base.toBigDecimal() + 0.01.toBigDecimal()).toDouble()
                person.listOfPrices.add(Price(base, 0.01))
                acc -= 1
            } else {
                person.subtotal = base
                person.listOfPrices.add(Price(base, 0.0))
            }
            person.tax = 0.0
            person.tip = 0.0
            updatePerson(person)
        }
    }

    private fun nullifyPersonalData() {
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.subtotal = null
            person.tax = null
            person.tip = null
            person.listOfPrices = ArrayDeque()
            updatePerson(person)
        }
    }

    private fun calculateResult() {
        val vs = viewState.value!!
        val subtotal = vs.subtotal ?: 0.0
        val tax = vs.tax ?: 0.0
        val tip = vs.tip ?: 0.0
        val grandTotal = (subtotal.toBigDecimal() + tax.toBigDecimal() + tip.toBigDecimal()).toDouble()
        var grandTotalRemainder = grandTotal.toBigDecimal()
        var taxRemainder = tax.toBigDecimal()
        for (person in vs.personList) {
            val personalBaseSubtotal = person.getBaseSubtotal()
            val dividedGrandTotal = Price.moneyDivider(personalBaseSubtotal * grandTotal * 100, (subtotal * 100).toInt())
            val grandTotalQuotient = dividedGrandTotal.base
            grandTotalRemainder -= grandTotalQuotient.toBigDecimal()
            val dividedTax = Price.moneyDivider(personalBaseSubtotal * tax * 100, (subtotal * 100).toInt())
            val taxQuotient = dividedTax.base
            taxRemainder -= taxQuotient.toBigDecimal()
            person.tax = taxQuotient
            person.tip = (grandTotalQuotient.toBigDecimal() - taxQuotient.toBigDecimal() - person.subtotal!!.toBigDecimal()).toDouble()
            updatePerson(person)
        }
        for (person in vs.personList.sorted()) {
            if (taxRemainder > BigDecimal.ZERO) {
                val personalTax = person.tax ?: 0.0
                person.tax = (personalTax.toBigDecimal() + 0.01.toBigDecimal()).toDouble()
                taxRemainder -= 0.01.toBigDecimal()
            }
            updatePerson(person)
        }
        for (person in vs.personList.sorted()) {
            if (grandTotalRemainder > BigDecimal.ZERO) {
                val personalTip = person.tip ?: 0.0
                person.tip = (personalTip.toBigDecimal() + 0.01.toBigDecimal()).toDouble()
                grandTotalRemainder -= 0.01.toBigDecimal()
            }
            updatePerson(person)
        }
        for (person in vs.personList) {
            Log.d("********", person.toString())
        }
        // personalTax = personalSubtotal / subtotal * tax = personalSubtotal * tax / subtotal
        // personalTip = personalSubtotal / subtotal * tip = personalSubtotal * tip / subtotal
    }

    private val dao = DivvieDatabase.getInstance(application).dao()

    private fun getAllPersonStatic() = dao.getAllPersonStatic()

    private fun findPerson(id: Int) = dao.findPerson(id)

    private fun insertPerson(person: Person) { dao.insertPerson(person) }

    private fun deletePerson(person: Person) = dao.deletePerson(person)

    private fun updatePerson(person: Person) {dao.updatePerson(person)}

    private fun deleteAllPerson() {dao.deleteAllPerson()}
}

