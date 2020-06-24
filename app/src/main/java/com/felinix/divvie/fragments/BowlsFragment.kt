package com.felinix.divvie.fragments

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
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
import com.felinix.divvie.*
import com.felinix.divvie.data.Person
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
        val priceAmount: TextView = view.findViewById(R.id.price_amount)
        val nameText: TextView = view.findViewById(R.id.name_text)
        currency.setTextColor(color)
        priceAmount.setTextColor(color)
        nameText.setTextColor(color)
        image.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private fun setVisibilityAttributes(i: Int, personList: Array<Person>, editable: Boolean, view: View) {
        val person = personList[i]
        val priceAmount: TextView = view.findViewById(R.id.price_amount)
        val price: LinearLayout = view.findViewById(R.id.price)
        val nameEdit: EditText = view.findViewById(R.id.name_edit)
        val nameText: TextView = view.findViewById(R.id.name_text)
        val personalSub = person.subtotal
        if (personalSub != null) {
            price.visibility = View.VISIBLE
            val personalTax: BigDecimal = person.tax ?: BigDecimal.ZERO
            val personalTip: BigDecimal = person.tip ?: BigDecimal.ZERO
            val personalTempPrice: BigDecimal = (person.tempPrice?.base ?: BigDecimal.ZERO) + (person.tempPrice?.acc ?: BigDecimal.ZERO)
            val total: BigDecimal = personalSub + personalTax + personalTip + personalTempPrice
            priceAmount.text = filter.clean(total.toPlainString())
        } else {
            price.visibility = View.GONE
        }

        if (editable) {
            nameEdit.visibility = View.VISIBLE
            nameText.visibility = View.GONE
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
            nameEdit.visibility = View.GONE
            nameText.visibility = View.VISIBLE
            nameText.text = personList[i].name
        }
    }

    private fun render(viewState: DivvieViewState) {
        for (i in 0 until MAX_GUESTS) {
            val view = bowlsList.getChildAt(i)
            view.isClickable = viewState.isSplittingBowls || viewState.isPersonalResult
            if (i < viewState.personList.size) {
                setVisibilityAttributes(i, viewState.personList, viewState.editableName, view)
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