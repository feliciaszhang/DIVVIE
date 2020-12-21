package com.felili.divvie.fragments

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.felili.divvie.*
import com.google.android.material.button.MaterialButtonToggleGroup
import com.tubitv.fragmentoperator.fragment.FoFragment
import com.tubitv.fragmentoperator.fragment.annotation.SingleInstanceFragment
import com.tubitv.fragments.FragmentOperator
import java.math.BigDecimal

@SingleInstanceFragment
class ResultFragment : FoFragment() {
    private lateinit var viewModel: DivvieViewModel
    private lateinit var subtotalET: DivvieEditText
    private lateinit var taxET: DivvieEditText
    private lateinit var currency: TextView
    private lateinit var percentage: TextView
    private lateinit var currencyTipET: DivvieEditText
    private lateinit var percentageTipET: EditText
    private lateinit var currencyButton: Button
    private lateinit var percentageButton: Button
    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var totalET: DivvieEditText
    private lateinit var backButton: Button
    private lateinit var restart: Button
    private lateinit var editTextCurrencyBackground: Drawable
    private lateinit var editTextPercentageBackground: Drawable
    private lateinit var tipHelper: TextView
    private lateinit var subtotalTitle: TextView
    private lateinit var taxTitle: TextView
    private lateinit var tipTitle: TextView
    private lateinit var totalTitle: TextView
    private lateinit var popupButton: ImageView
    private val filter = CurrencyInputFilter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.result_fragment, container, false)
        subtotalET = fragment.findViewById(R.id.subtotal_amount)
        taxET = fragment.findViewById(R.id.tax_amount)
        currency = fragment.findViewById(R.id.currency4)
        currencyTipET = fragment.findViewById(R.id.edit_tip_currency)
        percentage = fragment.findViewById(R.id.percentage)
        percentageTipET = fragment.findViewById(R.id.edit_tip_percentage)
        currencyButton = fragment.findViewById(R.id.currencyButton)
        percentageButton = fragment.findViewById(R.id.percentageButton)
        toggleGroup = fragment.findViewById(R.id.toggleButton)
        totalET = fragment.findViewById(R.id.total_amount)
        backButton = fragment.findViewById(R.id.back)
        restart = fragment.findViewById(R.id.restart)
        editTextCurrencyBackground = currencyTipET.background
        editTextPercentageBackground = percentageTipET.background
        tipHelper = fragment.findViewById(R.id.tip_helper)
        subtotalTitle = fragment.findViewById(R.id.subtotal_text)
        tipTitle = fragment.findViewById(R.id.tip_text)
        taxTitle = fragment.findViewById(R.id.tax_text)
        totalTitle = fragment.findViewById(R.id.total_text)
        popupButton = fragment.findViewById(R.id.popup)
        return fragment
    }

    private val currencyTextWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = currencyTipET.text.toString()
            currencyTipET.textSize = SizeCalculator(42f).resize(text)
            try {
                filter.clean(text)
                viewModel.onEvent(DivvieViewEvent.ResultEnterCurrencyTip("0" + text))
            } catch (e: java.lang.Exception) {
                viewModel.onEvent(DivvieViewEvent.InvalidCurrencyTip)
            }
        }
    }

    private val percentageTextWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = percentageTipET.text.toString()
            percentageTipET.textSize = SizeCalculator(42f).resize(text)
            viewModel.onEvent(DivvieViewEvent.ResultEnterPercentageTip("0" + text))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(DivvieViewEvent.DisplayResultFragment)
        viewModel.viewStateObservable.observe(viewLifecycleOwner, Observer { render(it) })

        currencyTipET.filters = arrayOf(filter)
        percentageTipET.filters = arrayOf(filter)

        currencyTipET.addTextChangedListener(currencyTextWatcher)

        percentageTipET.addTextChangedListener(percentageTextWatcher)

        currencyButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ResultSelectCurrency) }

        percentageButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ResultSelectPercentage) }

        toggleGroup.addOnButtonCheckedListener { group, checkedId, _ ->
            if (group.checkedButtonId == -1) group.check(checkedId)
        }

        currencyTipET.onFocusChangeListener = (View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = currencyTipET.text.toString()
                val cleanedText = try {
                    filter.clean(text)
                } catch (e: Exception) {
                    text
                }
                currencyTipET.setText(cleanedText)
            }
        })

        percentageTipET.onFocusChangeListener = (View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = percentageTipET.text.toString()
                percentageTipET.setText(filter.roundAndClean(text))
            }
        })

        backButton.setOnClickListener {
            FragmentOperator.showFragment(SplitFragment(), clearStack = false, skipOnPop = true)
        }

        restart.setOnClickListener {
            viewModel.onEvent(DivvieViewEvent.ResultToInput)
            FragmentOperator.showFragment(InputFragment(), clearStack = true, skipOnPop = true)
        }

        popupButton.setOnClickListener {
            val intent = Intent(context, PopupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun render(viewState: DivvieViewState) {
        val breakdownIndex = viewState.personalBreakDownIndex
        currencyTipET.isEnabled = breakdownIndex == null
        percentageTipET.isEnabled = breakdownIndex == null
        if (viewState.isCurrencyTip) {
            currency.visibility = View.VISIBLE
            currencyTipET.visibility = View.VISIBLE
            percentage.visibility = View.GONE
            percentageTipET.visibility = View.GONE
            currencyButton.isEnabled = false
            percentageButton.isEnabled = true
        } else {
            currency.visibility = View.GONE
            currencyTipET.visibility = View.GONE
            percentage.visibility = View.VISIBLE
            percentageTipET.visibility = View.VISIBLE
            currencyButton.isEnabled = true
            percentageButton.isEnabled = false
        }
        if (breakdownIndex != null) {
            popupButton.visibility = View.GONE
            popupButton.isEnabled = false
            currencyTipET.background = null
            percentageTipET.background = null
            subtotalTitle.text = resources.getString(R.string.personal_subtotal)
            tipTitle.text = resources.getString(R.string.personal_tip)
            taxTitle.text = resources.getString(R.string.personal_tax)
            totalTitle.text = resources.getString(R.string.personal_total)
            val person = viewState.personList[breakdownIndex]
            val personalSub = person.subtotal ?: BigDecimal.ZERO
            val personalTax = person.tax ?: BigDecimal.ZERO
            val personalTip = person.tip ?: BigDecimal.ZERO
            val personalGrandTotal = personalSub + personalTax + personalTip
            subtotalET.setText(filter.clean(personalSub.toPlainString()))
            taxET.setText(filter.clean(personalTax.toPlainString()))
            totalET.setText(filter.clean(personalGrandTotal.toPlainString()))
            subtotalET.textSize = SizeCalculator(42f).resize(personalSub.toPlainString())
            taxET.textSize = SizeCalculator(42f).resize(personalTax.toPlainString())
            totalET.textSize = SizeCalculator(42f).resize(personalGrandTotal.toPlainString())
            if (!viewState.isTipEditing) {
                currencyTipET.removeTextChangedListener(currencyTextWatcher)
                percentageTipET.removeTextChangedListener(percentageTextWatcher)
                currencyTipET.setText(filter.clean(personalTip.toPlainString()))
                if (personalSub == BigDecimal.ZERO) {
                    percentageTipET.setText(filter.roundAndClean("0"))
                } else {
                    percentageTipET.setText(filter.roundAndClean((personalTip * 100.toBigDecimal() / personalSub).toPlainString()))
                }
                currencyTipET.addTextChangedListener(currencyTextWatcher)
                percentageTipET.addTextChangedListener(percentageTextWatcher)
            }
        } else {
            popupButton.visibility = View.VISIBLE
            popupButton.isEnabled = true
            currencyTipET.background = editTextCurrencyBackground
            percentageTipET.background = editTextPercentageBackground
            subtotalTitle.text = resources.getString(R.string.subtotal)
            tipTitle.text = resources.getString(R.string.tip)
            taxTitle.text = resources.getString(R.string.tax)
            totalTitle.text = resources.getString(R.string.total)
            val totalSub = viewState.subtotal ?: BigDecimal.ZERO
            val totalTax = viewState.tax ?: BigDecimal.ZERO
            val totalTip = viewState.tip ?: BigDecimal.ZERO
            val grandTotal = totalSub + totalTax + totalTip
            subtotalET.setText(filter.clean(totalSub.toPlainString()))
            taxET.setText(filter.clean(totalTax.toPlainString()))
            totalET.setText(filter.clean(grandTotal.toPlainString()))
            subtotalET.textSize = SizeCalculator(42f).resize(totalSub.toPlainString())
            taxET.textSize = SizeCalculator(42f).resize(totalTax.toPlainString())
            totalET.textSize = SizeCalculator(42f).resize(grandTotal.toPlainString())
            if (viewState.tip != null && !viewState.isTipEditing) {
                currencyTipET.removeTextChangedListener(currencyTextWatcher)
                percentageTipET.removeTextChangedListener(percentageTextWatcher)
                currencyTipET.setText(filter.clean(totalTip.toPlainString()))
                percentageTipET.setText(filter.roundAndClean((totalTip * 100.toBigDecimal() / totalSub).toPlainString()))
                currencyTipET.addTextChangedListener(currencyTextWatcher)
                percentageTipET.addTextChangedListener(percentageTextWatcher)
            }
        }
        if ((viewState.tip ?: BigDecimal.ZERO > viewState.subtotal ?: BigDecimal.ZERO) && breakdownIndex == null) {
            currencyTipET.setText(filter.clean(viewState.subtotal?.toPlainString() ?: "0.00"))
            percentageTipET.setText("100")
        }
        if (viewState.invalidCurrencyTip && viewState.isCurrencyTip) {
            totalET.setText("")
            percentageButton.isEnabled = false
            tipHelper.visibility = View.VISIBLE
            tipHelper.setTextColor(resources.getColor(R.color.colorAccent, context!!.theme))
            currencyTipET.background.colorFilter = PorterDuffColorFilter(
                resources.getColor(R.color.colorAccent, context!!.theme), PorterDuff.Mode.SRC_ATOP)
        }
        if (!viewState.invalidCurrencyTip) {
            percentageButton.isEnabled = true
            tipHelper.visibility = View.INVISIBLE
            if (viewState.personalBreakDownIndex == null) {
                currencyTipET.background.colorFilter = PorterDuffColorFilter(
                    resources.getColor(R.color.colorLight, context!!.theme),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }
    }
}