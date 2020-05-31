package com.example.divvie.fragments

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
import android.widget.LinearLayout
import com.example.divvie.*


class ResultFragment : Fragment() {
    companion object {
        fun newInstance() = ResultFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var subtotal: TextView
    private lateinit var tax: TextView
    private lateinit var currencyTipGroup: LinearLayout
    private lateinit var percentageTipGroup: LinearLayout
    private lateinit var currencyTip: EditText
    private lateinit var percentageTip: EditText
    private lateinit var currencyButton: Button
    private lateinit var percentageButton: Button
    private lateinit var total: TextView
    private lateinit var backButton: Button
    private lateinit var restart: Button
    private lateinit var editTextCurrencyBackground: Drawable
    private lateinit var editTextPercentageBackground: Drawable
    private val filter = CurrencyInputFilter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.result_fragment, container, false)
        subtotal = fragment.findViewById(R.id.subtotal_amount)
        tax = fragment.findViewById(R.id.tax_amount)
        currencyTipGroup = fragment.findViewById(R.id.currency_tip_group)
        currencyTip = fragment.findViewById(R.id.edit_tip_currency)
        percentageTipGroup = fragment.findViewById(R.id.percentage_tip_group)
        percentageTip = fragment.findViewById(R.id.edit_tip_percentage)
        currencyButton = fragment.findViewById(R.id.currencyButton)
        percentageButton = fragment.findViewById(R.id.percentageButton)
        total = fragment.findViewById(R.id.total_amount)
        backButton = fragment.findViewById(R.id.back)
        restart = fragment.findViewById(R.id.restart)
        editTextCurrencyBackground = currencyTip.background
        editTextPercentageBackground = percentageTip.background
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(ResultViewEvent.DisplayFragment)
        viewModel.viewStateObservable.observe(viewLifecycleOwner, Observer { render(it) })

        currencyTip.filters = arrayOf(filter)
        percentageTip.filters = arrayOf(filter)

        currencyTip.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEvent(ResultViewEvent.EnterCurrencyTip(currencyTip.text.toString()))
            }
        })

        percentageTip.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEvent(ResultViewEvent.EnterPercentageTip(percentageTip.text.toString()))
            }
        })

        backButton.setOnClickListener {
            viewModel.onEvent(ResultViewEvent.Back)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, SplitFragment.newInstance()
            ).commit()
        }

        restart.setOnClickListener {
            viewModel.onEvent(ResultViewEvent.Restart)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, InputFragment.newInstance()
            ).commit()
        }

        currencyButton.setOnClickListener { viewModel.onEvent(ResultViewEvent.SelectCurrency) }

        percentageButton.setOnClickListener { viewModel.onEvent(ResultViewEvent.SelectPercentage) }
    }

    private fun render(viewState: DivvieViewState) {
        val breakdownIndex = viewState.personalBreakDownIndex
        currencyTip.isEnabled = breakdownIndex == null
        percentageTip.isEnabled = breakdownIndex == null
        if (viewState.isCurrencyTip) {
            currencyTipGroup.visibility = View.VISIBLE
            percentageTipGroup.visibility = View.GONE
            currencyButton.isEnabled = false
            percentageButton.isEnabled = true
        } else {
            currencyTipGroup.visibility = View.GONE
            percentageTipGroup.visibility = View.VISIBLE
            currencyButton.isEnabled = true
            percentageButton.isEnabled = false
        }
        if (breakdownIndex != null) {
            currencyTip.background = null
            percentageTip.background = null
            val person = viewState.personList[breakdownIndex]
            val personalSub = person.subtotal ?: 0.0
            val personalTax = person.tax ?: 0.0
            val personalTip = person.tip ?: 0.0
            subtotal.text = filter.convert(personalSub.toString())
            tax.text = filter.convert(personalTax.toString())
            total.text = filter.convert((personalSub + personalTax + personalTip).toString())
            if (!viewState.isTipEditing) {
                currencyTip.setText(filter.convert(personalTip.toString()))
                percentageTip.setText((personalTip * 100 / personalSub).toString())
            }
        } else {
            currencyTip.background = editTextCurrencyBackground
            percentageTip.background = editTextPercentageBackground
            val totalSub = viewState.subtotal ?: 0.0
            val totalTax = viewState.tax ?: 0.0
            val totalTip = viewState.tip ?: 0.0
            subtotal.text = filter.convert(totalSub.toString())
            tax.text = filter.convert(totalTax.toString())
            total.text = filter.convert((totalSub + totalTax + totalTip).toString())
            if (viewState.tip != null && !viewState.isTipEditing) {
                currencyTip.setText(filter.convert(totalTip.toString()))
                percentageTip.setText((totalTip * 100 / totalSub).toString())
            }
        }
    }
}