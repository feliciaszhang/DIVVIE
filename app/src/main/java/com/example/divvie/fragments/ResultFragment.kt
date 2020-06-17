package com.example.divvie.fragments

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
import com.example.divvie.*
import java.math.BigDecimal


class ResultFragment : Fragment() {
    companion object {
        fun newInstance() = ResultFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var subtotal: TextView
    private lateinit var tax: TextView
    private lateinit var currency: TextView
    private lateinit var percentage: TextView
    private lateinit var currencyTip: EditText
    private lateinit var percentageTip: EditText
    private lateinit var currencyButton: Button
    private lateinit var percentageButton: Button
    private lateinit var total: TextView
    private lateinit var backButton: Button
    private lateinit var restart: Button
    private lateinit var editTextCurrencyBackground: Drawable
    private lateinit var editTextPercentageBackground: Drawable
    private lateinit var tipHelper: TextView
    private val filter = CurrencyInputFilter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.result_fragment, container, false)
        subtotal = fragment.findViewById(R.id.subtotal_amount)
        tax = fragment.findViewById(R.id.tax_amount)
        currency = fragment.findViewById(R.id.currency4)
        currencyTip = fragment.findViewById(R.id.edit_tip_currency)
        percentage = fragment.findViewById(R.id.percentage)
        percentageTip = fragment.findViewById(R.id.edit_tip_percentage)
        currencyButton = fragment.findViewById(R.id.currencyButton)
        percentageButton = fragment.findViewById(R.id.percentageButton)
        total = fragment.findViewById(R.id.total_amount)
        backButton = fragment.findViewById(R.id.back)
        restart = fragment.findViewById(R.id.restart)
        editTextCurrencyBackground = currencyTip.background
        editTextPercentageBackground = percentageTip.background
        tipHelper = fragment.findViewById(R.id.tip_helper)
        return fragment
    }

    private val currencyTextWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            try {
                filter.clean(currencyTip.text.toString())
                viewModel.onEvent(DivvieViewEvent.ResultEnterCurrencyTip("0" + currencyTip.text.toString()))
            } catch (e: java.lang.Exception) {
                viewModel.onEvent(DivvieViewEvent.InvalidCurrencyTip)
            }
        }
    }

    private val percentageTextWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.onEvent(DivvieViewEvent.ResultEnterPercentageTip("0" + percentageTip.text.toString()))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(DivvieViewEvent.DisplayResultFragment)
        viewModel.viewStateObservable.observe(viewLifecycleOwner, Observer { render(it) })

        currencyTip.filters = arrayOf(filter)
        percentageTip.filters = arrayOf(filter)

        currencyTip.addTextChangedListener(currencyTextWatcher)

        percentageTip.addTextChangedListener(percentageTextWatcher)

        currencyButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ResultSelectCurrency) }

        percentageButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ResultSelectPercentage) }

        currencyTip.onFocusChangeListener = (View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = currencyTip.text.toString()
                val cleanedText = try {
                    filter.clean(text)
                } catch (e: Exception) {
                    text
                }
                currencyTip.setText(cleanedText)
            }
        })

        percentageTip.onFocusChangeListener = (View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = percentageTip.text.toString()
                percentageTip.setText(filter.clean(text))
            }
        })

        backButton.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, SplitFragment.newInstance()
            ).commit()
        }

        restart.setOnClickListener {
            viewModel.onEvent(DivvieViewEvent.ResultToInput)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, InputFragment.newInstance()
            ).commit()
        }
    }

    private fun render(viewState: DivvieViewState) {
        val breakdownIndex = viewState.personalBreakDownIndex
        currencyTip.isEnabled = breakdownIndex == null
        percentageTip.isEnabled = breakdownIndex == null
        if (viewState.isCurrencyTip) {
            currency.visibility = View.VISIBLE
            currencyTip.visibility = View.VISIBLE
            percentage.visibility = View.GONE
            percentageTip.visibility = View.GONE
            currencyButton.isEnabled = false
            percentageButton.isEnabled = true
        } else {
            currency.visibility = View.GONE
            currencyTip.visibility = View.GONE
            percentage.visibility = View.VISIBLE
            percentageTip.visibility = View.VISIBLE
            currencyButton.isEnabled = true
            percentageButton.isEnabled = false
        }
        if (breakdownIndex != null) {
            currencyTip.background = null
            percentageTip.background = null
            val person = viewState.personList[breakdownIndex]
            val personalSub = person.subtotal ?: BigDecimal.ZERO
            val personalTax = person.tax ?: BigDecimal.ZERO
            val personalTip = person.tip ?: BigDecimal.ZERO
            val personalGrandTotal = personalSub + personalTax + personalTip
            subtotal.text = filter.clean(personalSub.toPlainString())
            tax.text = filter.clean(personalTax.toPlainString())
            total.text = filter.clean(personalGrandTotal.toPlainString())
            if (!viewState.isTipEditing) {
                currencyTip.removeTextChangedListener(currencyTextWatcher)
                percentageTip.removeTextChangedListener(percentageTextWatcher)
                currencyTip.setText(filter.clean(personalTip.toPlainString()))
                percentageTip.setText(filter.roundAndClean(personalTip * 100.toBigDecimal() / personalSub))
                currencyTip.addTextChangedListener(currencyTextWatcher)
                percentageTip.addTextChangedListener(percentageTextWatcher)
            }
        } else {
            currencyTip.background = editTextCurrencyBackground
            percentageTip.background = editTextPercentageBackground
            val totalSub = viewState.subtotal ?: BigDecimal.ZERO
            val totalTax = viewState.tax ?: BigDecimal.ZERO
            val totalTip = viewState.tip ?: BigDecimal.ZERO
            val grandTotal = totalSub + totalTax + totalTip
            subtotal.text = filter.clean(totalSub.toPlainString())
            tax.text = filter.clean(totalTax.toPlainString())
            total.text = filter.clean(grandTotal.toPlainString())
            if (viewState.tip != null && !viewState.isTipEditing) {
                currencyTip.removeTextChangedListener(currencyTextWatcher)
                percentageTip.removeTextChangedListener(percentageTextWatcher)
                currencyTip.setText(filter.clean(totalTip.toPlainString()))
                percentageTip.setText(filter.roundAndClean(totalTip * 100.toBigDecimal() / totalSub))
                currencyTip.addTextChangedListener(currencyTextWatcher)
                percentageTip.addTextChangedListener(percentageTextWatcher)
            }
        }
        if (viewState.invalidCurrencyTip) {
            percentageButton.isEnabled = false
            tipHelper.visibility = View.VISIBLE
            tipHelper.setTextColor(resources.getColor(R.color.colorAccent, context!!.theme))
            currencyTip.background.colorFilter = PorterDuffColorFilter(
                resources.getColor(R.color.colorAccent, context!!.theme), PorterDuff.Mode.SRC_ATOP)
        }
        if (!viewState.invalidCurrencyTip) {
            percentageButton.isEnabled = true
            tipHelper.visibility = View.INVISIBLE
            if (viewState.personalBreakDownIndex == null) {
                currencyTip.background.colorFilter = PorterDuffColorFilter(
                    resources.getColor(R.color.colorLight, context!!.theme),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }
    }
}