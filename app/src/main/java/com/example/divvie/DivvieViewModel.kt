package com.example.divvie

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.divvie.database.DivvieDatabase
import com.example.divvie.database.Person

open class DivvieViewModel<ViewState>(
    app: Application,
    initialViewState: ViewState
) : AndroidViewModel(app) {

    private val viewState = MutableLiveData<ViewState>()
    val viewStateObservable: LiveData<ViewState>
        get() = viewState
    protected var currentViewState: ViewState = initialViewState
        set(value) {
            field = value
            viewState.value = value
        }

    private val dao = DivvieDatabase.getInstance(app).dao()

    protected fun getAllPersonStatic() = dao.getAllPersonStatic()

    protected fun getAllPerson() = dao.getAllPerson()

    protected fun findPerson(id: Int) = dao.findPerson(id)

    protected fun insertPerson(person: Person) { dao.insertPerson(person) }

    protected fun deletePerson(person: Person) = dao.deletePerson(person)

    protected fun getNumberOfPeople() = dao.getNumberOfPeople()

    protected fun getNumberOfPeopleStatic() = dao.getNumberOfPeopleStatic()

    protected fun updatePerson(person: Person) {dao.updatePerson(person)}

    protected var finalSubtotal: Double = AMOUNT_DEFAULT

    protected var finalTax: Double = AMOUNT_DEFAULT
}