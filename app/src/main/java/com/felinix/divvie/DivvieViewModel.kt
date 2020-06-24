package com.felinix.divvie

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.felinix.divvie.data.DivvieDatabase
import com.felinix.divvie.data.Person
import com.felinix.divvie.data.Price
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class DivvieViewModel(application: Application) : AndroidViewModel(application) {

    private val viewState = MutableLiveData<DivvieViewState>()
    val viewStateObservable: LiveData<DivvieViewState>
        get() = viewState

    fun onEvent(event: DivvieViewEvent) {
        return when (event) {
            is DivvieViewEvent.DisplayActivity -> onDisplayActivity()
            is DivvieViewEvent.InvalidSubtotal -> onInvalidSubtotal()
            is DivvieViewEvent.InvalidTax -> onInvalidTax()
            is DivvieViewEvent.InvalidItem -> onInvalidItem()
            is DivvieViewEvent.InvalidCurrencyTip -> onInvalidCurrencyTip()

            is DivvieViewEvent.DisplayInputFragment -> onDisplayInputFragment()
            is DivvieViewEvent.InputInsertPerson -> onInputInsertPerson()
            is DivvieViewEvent.InputRemovePerson -> onInputRemovePerson()
            is DivvieViewEvent.InputEnterSubtotal -> onInputEnterSubtotal(event.input)
            is DivvieViewEvent.InputEnterTax -> onInputEnterTax(event.input)
            is DivvieViewEvent.InputToSplit -> onInputToSplit()

            is DivvieViewEvent.DisplaySplitFragment -> onDisplaySplitFragment()

            is DivvieViewEvent.DisplayItemFragment -> onDisplayItemFragment()
            is DivvieViewEvent.ItemEnterPrice -> onItemEnterPrice(event.input)
            is DivvieViewEvent.ItemDone -> onItemDone()
            is DivvieViewEvent.ItemNext -> onItemNext()
            is DivvieViewEvent.ItemUndo -> onItemUndo()
            is DivvieViewEvent.ItemClear -> onItemClear()

            is DivvieViewEvent.DisplayResultFragment -> onDisplayResultFragment()
            is DivvieViewEvent.ResultEnterCurrencyTip -> onResultEnterCurrencyTip(event.input)
            is DivvieViewEvent.ResultEnterPercentageTip -> onResultEnterPercentageTip(event.input)
            is DivvieViewEvent.ResultSelectCurrency -> onResultSelectCurrency()
            is DivvieViewEvent.ResultSelectPercentage -> onResultSelectPercentage()
            is DivvieViewEvent.ResultToInput -> onResultToInput()

            is DivvieViewEvent.BowlsEnterName -> onBowlsEnterName(event.i, event.input)
            is DivvieViewEvent.BowlsSplitPrice -> onBowlsSplitPrice(event.i)
            is DivvieViewEvent.BowlsViewBreakdown -> onBowlsViewBreakdown(event.i)
        }
    }

    private fun onDisplayActivity() {
        deleteAllPerson()
        viewState.value = DivvieViewState.defaultViewState()
        for (person in viewState.value!!.personList) {
            insertPerson(person)
        }
    }

    private fun onInvalidSubtotal() {
        viewState.value = viewState.value!!.copy(
            invalidSubtotal = true
        )
    }

    private fun onInvalidTax() {
        viewState.value = viewState.value!!.copy(
            invalidTax = true
        )
    }

    private fun onInvalidItem() {
        viewState.value = viewState.value!!.copy(
            invalidItem = true
        )
    }

    private fun onInvalidCurrencyTip() {
        viewState.value = viewState.value!!.copy(
            isPersonalResult = false,
            invalidCurrencyTip = true
        )
    }

    private fun onBowlsViewBreakdown(i: Int?) {
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

    private fun onBowlsEnterName(i: Int, input: String) {
        if (i < getAllPersonStatic().size) {
            val person = findPerson(i)
            person.name = input
            updatePerson(person)
            viewState.value = viewState.value!!.copy(
                personList = getAllPersonStatic()
            )
        }
    }

    private fun onBowlsSplitPrice(i: Int) {
        val vs = viewState.value!!
        val listOfIndex = vs.tempItemListOfIndex
        if (listOfIndex.contains(i)) {
            listOfIndex.remove(i)
        } else {
            listOfIndex.add(i)
        }
        val itemPrice = vs.tempItemPrice ?: BigDecimal.ZERO
        val dividedMoney = Price.moneyDivider(itemPrice, listOfIndex.size)
        val base = dividedMoney.base
        var acc = dividedMoney.acc
        for (person in vs.personList.sorted()) {
            if (person.id in listOfIndex) {
                if (acc > BigDecimal.ZERO) {
                    person.tempPrice = Price(base, 0.01.toBigDecimal())
                    acc -= BigDecimal.ONE
                } else {
                    person.tempPrice = Price(base, BigDecimal.ZERO)
                }
            } else {
                person.tempPrice = Price(BigDecimal.ZERO, BigDecimal.ZERO)
            }
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            tempItemListOfIndex = listOfIndex,
            personList = getAllPersonStatic()
        )
    }

    private fun onDisplayInputFragment() { // onSplitToInput, onResultToInput
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.subtotal = null
            person.tax = null
            person.tip = null
            person.tempPrice = null
            person.listOfPrices = ArrayDeque()
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            isSubtotalEditing = false, isTaxEditing = false, isSplittingBowls = false,
            editableName = true, personList = getAllPersonStatic(), tip = null,
            isTipEditing = false, leftover = null, isCurrencyTip = true, tempItemPrice = null,
            tempItemListOfIndex = ArrayList(), itemList = ArrayDeque(), isItemEditing = false,
            isPersonalResult = false, personalBreakDownIndex = null, invalidSubtotal = false,
            invalidTax = false, invalidItem = false, invalidCurrencyTip = false
        ) // subtotal, tax, leftover, personList
    }

    private fun onInputInsertPerson() {
        val num = viewState.value!!.personList.size
        if (num < MAX_GUESTS) {
            insertPerson(Person(id = num))
        }
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onInputRemovePerson() {
        val num = viewState.value!!.personList.size
        if (num > MIN_GUESTS) {
            deletePerson(Person(id = num - 1))
        }
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onInputEnterSubtotal(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(
                invalidSubtotal = false,
                subtotal = input.toBigDecimal(),
                isSubtotalEditing = true,
                leftover = input.toBigDecimal()
            )
        } else {
            viewState.value = viewState.value!!.copy(
                invalidSubtotal = false,
                subtotal = BigDecimal.ZERO,
                isSubtotalEditing = true,
                leftover = BigDecimal.ZERO
            )
        }
    }

    private fun onInputEnterTax(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(
                invalidTax = false,
                tax = input.toBigDecimal(),
                isTaxEditing = true
            )
        } else {
            viewState.value = viewState.value!!.copy(
                invalidTax = false,
                tax = BigDecimal.ZERO,
                isTaxEditing = true
            )
        }
    }

    private fun onInputToSplit() {
        if (viewState.value!!.tax == null) {
            viewState.value = viewState.value!!.copy(
                tax = BigDecimal.ZERO
            )
        }
    }

    private fun onDisplaySplitFragment() { // onInputToSplit, onItemToSplit, onResultToSplit
        val vs = viewState.value!!
        val dividedMoney = Price.moneyDivider(vs.subtotal ?: BigDecimal.ZERO, vs.personList.size)
        val base = dividedMoney.base
        var acc = dividedMoney.acc
        for (person in vs.personList) {
            person.tax = BigDecimal.ZERO
            person.tip = BigDecimal.ZERO
            person.tempPrice = null
            person.listOfPrices = ArrayDeque()
            if (acc > BigDecimal.ZERO) {
                person.subtotal = base + 0.01.toBigDecimal()
                person.listOfPrices.add(Price(base, 0.01.toBigDecimal()))
                acc -= BigDecimal.ONE
            } else {
                person.subtotal = base
                person.listOfPrices.add(Price(base, BigDecimal.ZERO))
            }
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            isSubtotalEditing = false, isTaxEditing = false, isSplittingBowls = false,
            editableName = false, personList = getAllPersonStatic(), tip = null,
            isTipEditing = false, leftover = null, isCurrencyTip = true, tempItemPrice = null,
            tempItemListOfIndex = ArrayList(), itemList = ArrayDeque(), isItemEditing = false,
            isPersonalResult = false, personalBreakDownIndex = null, invalidSubtotal = false,
            invalidTax = false, invalidItem = false, invalidCurrencyTip = false
        ) // subtotal, tax, editableName, personList
    }

    private fun onDisplayResultFragment() { // onSplitToResult, onCalculateToResult
        calculateResult()
        viewState.value = viewState.value!!.copy(
            isSubtotalEditing = false, isTaxEditing = false, isSplittingBowls = false,
            editableName = false, personList = getAllPersonStatic(), tip = null,
            isTipEditing = false, leftover = BigDecimal.ZERO, isCurrencyTip = true, tempItemPrice = null,
            tempItemListOfIndex = ArrayList(), isItemEditing = false, isPersonalResult = true,
            personalBreakDownIndex = null, invalidSubtotal = false, invalidTax = false,
            invalidItem = false, invalidCurrencyTip = false
        ) // subtotal, tax, editableName, isPersonalResult, leftover, personList, itemList
    }

    private fun onResultEnterCurrencyTip(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(
                isPersonalResult = true,
                invalidCurrencyTip = false,
                tip = input.toBigDecimal(),
                isTipEditing = true
            )
        } else {
            viewState.value = viewState.value!!.copy(
                isPersonalResult = true,
                invalidCurrencyTip = false,
                tip = BigDecimal.ZERO,
                isTipEditing = true
            )
        }
        calculateResult()
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onResultEnterPercentageTip(input: String) {
        if (input != "") {
            val subtotal = viewState.value!!.subtotal ?: BigDecimal.ZERO
            val dividedMoney = Price.moneyDivider(input.toBigDecimal() * subtotal, 100)
            viewState.value = viewState.value!!.copy(
                tip = dividedMoney.base,
                isTipEditing = true
            )
        } else {
            viewState.value = viewState.value!!.copy(
                tip = BigDecimal.ZERO,
                isTipEditing = true
            )
        }
        calculateResult()
        viewState.value = viewState.value!!.copy(
            personList = getAllPersonStatic()
        )
    }

    private fun onResultSelectCurrency() {
        viewState.value = viewState.value!!.copy(
            isCurrencyTip = true,
            isTipEditing = false
        )
    }

    private fun onResultSelectPercentage() {
        viewState.value = viewState.value!!.copy(
            isCurrencyTip = false,
            isTipEditing = false
        )
    }

    private fun onResultToInput() {
        val savedPersonData = getAllPersonStatic()
        viewState.value = DivvieViewState.defaultViewState()
        viewState.value = viewState.value!!.copy(
            personList = savedPersonData
        )
    }

    private fun onDisplayItemFragment() { // onSplitToItem, onCalculateToItem
        val vs = viewState.value!!
        for (person in vs.personList) {
            person.subtotal = BigDecimal.ZERO
            person.tip = BigDecimal.ZERO
            person.tax = BigDecimal.ZERO
            person.tempPrice = Price(BigDecimal.ZERO, BigDecimal.ZERO)
            person.listOfPrices = ArrayDeque()
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            isSubtotalEditing = false, isTaxEditing = false, isSplittingBowls = false,
            editableName = false, personList = getAllPersonStatic(), tip = null,
            isTipEditing = false, leftover = vs.subtotal, isCurrencyTip = true,
            tempItemPrice = BigDecimal.ZERO, tempItemListOfIndex = ArrayList(), itemList = ArrayDeque(),
            isItemEditing = false, isPersonalResult = false, personalBreakDownIndex = null,
            invalidSubtotal = false, invalidTax = false, invalidItem = false, invalidCurrencyTip = false
        ) // subtotal, tax, editableName, personList, leftover
    }

    private fun onItemEnterPrice(input: String) {
        if (input != "") {
            viewState.value = viewState.value!!.copy(
                invalidItem = false,
                tempItemPrice = input.toBigDecimal(),
                isItemEditing = true
            )
        } else {
            viewState.value = viewState.value!!.copy(
                invalidItem = false,
                tempItemPrice = BigDecimal.ZERO,
                isItemEditing = true
            )
        }
    }

    private fun onItemNext() {
        viewState.value = viewState.value!!.copy(
            isSplittingBowls = true
        )
    }

    private fun onItemDone() {
        val vs = viewState.value!!
        val itemPrice = vs.tempItemPrice ?: BigDecimal.ZERO
        vs.itemList.add(itemPrice)
        val leftover = vs.leftover ?: BigDecimal.ZERO
        for (person in vs.personList) {
            val personalTemp = person.tempPrice ?: Price(BigDecimal.ZERO, BigDecimal.ZERO)
            person.subtotal = (person.subtotal ?: BigDecimal.ZERO + personalTemp.base + personalTemp.acc)
            person.listOfPrices.add(personalTemp)
            person.tempPrice = Price(BigDecimal.ZERO, BigDecimal.ZERO)
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            tempItemPrice = BigDecimal.ZERO,
            tempItemListOfIndex = ArrayList(),
            itemList = vs.itemList,
            leftover = leftover - itemPrice,
            isSplittingBowls = false,
            isItemEditing = false,
            personList = getAllPersonStatic()
        )
    }

    private fun onItemUndo() {
        val vs = viewState.value!!
        if (vs.isSplittingBowls) {
            for (person in vs.personList) {
                person.tempPrice = Price(BigDecimal.ZERO, BigDecimal.ZERO)
                updatePerson(person)
            }
            viewState.value = viewState.value!!.copy(
                tempItemPrice = BigDecimal.ZERO,
                tempItemListOfIndex = ArrayList(),
                isSplittingBowls = false,
                isItemEditing = false,
                personList = getAllPersonStatic()

            )
        } else {
            val removedItem = vs.itemList.removeLast()
            val leftover = vs.leftover ?: BigDecimal.ZERO
            for (person in vs.personList) {
                val lastPrice = person.listOfPrices.removeLast()
                person.subtotal = person.subtotal ?: BigDecimal.ZERO - lastPrice.base - lastPrice.acc
                updatePerson(person)
            }
            viewState.value = viewState.value!!.copy(
                itemList = vs.itemList,
                leftover = leftover + removedItem,
                isItemEditing = false,
                personList = getAllPersonStatic()
            )
        }
    }

    private fun onItemClear() {
        val vs = viewState.value!!
        vs.itemList.clear()
        for (person in vs.personList) {
            person.subtotal = BigDecimal.ZERO
            person.tax = BigDecimal.ZERO
            person.tip = BigDecimal.ZERO
            person.tempPrice = Price(BigDecimal.ZERO, BigDecimal.ZERO)
            person.listOfPrices.clear()
            updatePerson(person)
        }
        viewState.value = viewState.value!!.copy(
            itemList = ArrayDeque(),
            leftover = vs.subtotal,
            isItemEditing = false,
            tempItemPrice = BigDecimal.ZERO,
            tempItemListOfIndex = ArrayList(),
            isSplittingBowls = false,
            personList = getAllPersonStatic()
        )
    }

    private fun calculateResult() {
        val vs = viewState.value!!
        val subtotal = vs.subtotal ?: BigDecimal.ZERO
        val tax = vs.tax ?: BigDecimal.ZERO
        val tip = vs.tip ?: BigDecimal.ZERO
        val grandTotal = subtotal + tax + tip
        var grandTotalRemainder = grandTotal
        var taxRemainder = tax
        for (person in vs.personList) {
            val personalBaseSubtotal = person.getBaseSubtotal()
            val dividedGrandTotal = Price.moneyDivider(personalBaseSubtotal * grandTotal * 100.toBigDecimal(), (subtotal * 100.toBigDecimal()).toInt())
            val grandTotalQuotient = dividedGrandTotal.base
            grandTotalRemainder -= grandTotalQuotient
            val dividedTax = Price.moneyDivider(personalBaseSubtotal * tax * 100.toBigDecimal(), (subtotal * 100.toBigDecimal()).toInt())
            val taxQuotient = dividedTax.base
            taxRemainder -= taxQuotient
            person.tax = taxQuotient
            person.tip = grandTotalQuotient - taxQuotient - (person.subtotal ?: BigDecimal.ZERO)
            updatePerson(person)
        }
        for (person in vs.personList.sorted()) {
            if (taxRemainder > BigDecimal.ZERO) {
                val personalTax = person.tax ?: BigDecimal.ZERO
                person.tax = personalTax + 0.01.toBigDecimal()
                taxRemainder -= 0.01.toBigDecimal()
            }
            updatePerson(person)
        }
        for (person in vs.personList.sorted()) {
            if (grandTotalRemainder > BigDecimal.ZERO) {
                val personalTip = person.tip ?: BigDecimal.ZERO
                person.tip = personalTip + 0.01.toBigDecimal()
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

