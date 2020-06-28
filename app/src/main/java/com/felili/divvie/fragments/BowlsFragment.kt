package com.felili.divvie.fragments

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.felili.divvie.*
import com.felili.divvie.data.Person
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import java.math.BigDecimal

class BowlsFragment : Fragment() {
    companion object {
        fun newInstance() = BowlsFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var bowlsList: ConstraintLayout
    private lateinit var etBackground: Drawable
    private val filter = CurrencyInputFilter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.bowls_fragment, container, false)
        bowlsList = fragment.findViewById(R.id.bowls)
        for (i in 0 until MAX_GUESTS) {
            changeColor(bowlsList.getChildAt(i), resources.getColor(R.color.colorWhite, context!!.theme))
        }
        etBackground = fragment.findViewById<DivvieEditText>(R.id.name_edit).background
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.viewStateObservable.observe(viewLifecycleOwner, Observer { render(it) })
    }

    private fun changeColor(view: View, color: Int) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val currency: TextView = view.findViewById(R.id.currency)
        val priceAmount: DivvieEditText = view.findViewById(R.id.price_amount)
        currency.setTextColor(color)
        priceAmount.setTextColor(color)
        image.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private fun setVisibilityAttributes(i: Int, personList: Array<Person>, editable: Boolean, invalid: Boolean, view: View) {
        val person = personList[i]
        val priceAmount: DivvieEditText = view.findViewById(R.id.price_amount)
        val price: LinearLayout = view.findViewById(R.id.price)
        val nameEdit: EditText = view.findViewById(R.id.name_edit)
        val personalSub = person.subtotal
        if (personalSub != null) {
            price.visibility = View.VISIBLE
            val personalTax: BigDecimal = person.tax ?: BigDecimal.ZERO
            val personalTip: BigDecimal = person.tip ?: BigDecimal.ZERO
            val personalTempPrice: BigDecimal = (person.tempPrice?.base ?: BigDecimal.ZERO) + (person.tempPrice?.acc ?: BigDecimal.ZERO)
            val total: BigDecimal = personalSub + personalTax + personalTip + personalTempPrice
            priceAmount.setText(filter.clean(total.toPlainString()))
        } else {
            price.visibility = View.INVISIBLE
        }
        if (invalid) {
            priceAmount.setText("")
        }
        if (editable) {
            nameEdit.isEnabled = true
            nameEdit.background = etBackground
            nameEdit.hint = resources.getString(R.string.enter_name)
            nameEdit.setText(personList[i].name)
            nameEdit.onFocusChangeListener = (OnFocusChangeListener { _, hasFocus -> if (!hasFocus) {
                viewModel.onEvent(DivvieViewEvent.BowlsEnterName(i, nameEdit.text.toString())) }
            })
            nameEdit.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(nameEdit.windowToken, 0)
                    true
                } else {
                    false
                }
            }
        } else {
            nameEdit.isEnabled = false
            nameEdit.background = null
            nameEdit.hint = ""
        }
    }

    private fun render(viewState: DivvieViewState) {
        for (i in 0 until MAX_GUESTS) {
            val view = bowlsList.getChildAt(i)
            view.isClickable = viewState.isSplittingBowls || viewState.isPersonalResult
            if (i < viewState.personList.size) {
                setVisibilityAttributes(i, viewState.personList, viewState.editableName, viewState.invalidCurrencyTip, view)
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
            if (viewState.isSplittingBowls) {
                if (viewState.tempItemListOfIndex.contains(i)) {
                    changeColor(view, resources.getColor(R.color.colorAccent, context!!.theme))
                } else {
                    changeColor(view, resources.getColor(R.color.colorSemiLight, context!!.theme))
                }
                view.setOnClickListener {
                    viewModel.onEvent(DivvieViewEvent.BowlsSplitPrice(i))
                }
            } else {
                changeColor(view, resources.getColor(R.color.colorWhite, context!!.theme))
            }
            if (viewState.isPersonalResult) {
                view.setOnClickListener { viewModel.onEvent(DivvieViewEvent.BowlsViewBreakdown(i)) }
                when (viewState.personalBreakDownIndex) {
                    null -> changeColor(view, resources.getColor(R.color.colorWhite, context!!.theme))
                    i -> changeColor(view, resources.getColor(R.color.colorAccent, context!!.theme))
                    else -> changeColor(view, resources.getColor(R.color.colorSemiLight, context!!.theme))
                }
            }
        }
    }
}