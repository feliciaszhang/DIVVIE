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
import android.content.Intent
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
    private lateinit var startOver: Button

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
        startOver = fragment.findViewById(R.id.start_over)
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(ResultViewEvent.DisplayFragment)
        viewModel.totalObservable.observe(viewLifecycleOwner, Observer { displayTotal(it) })
        viewModel.isCurrencyObservable.observe(viewLifecycleOwner, Observer { setTipState(it) })

        // TODO ViewState
        subtotal.text = viewModel.getSubtotal().toString()

        tax.text = viewModel.getTax().toString()

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

        currencyButton.setOnClickListener {
            viewModel.onEvent(ResultViewEvent.SelectCurrency)
        }

        percentageButton.setOnClickListener {
            viewModel.onEvent(ResultViewEvent.SelectPercentage)
        }

        backButton.setOnClickListener {
            viewModel.onEvent(ResultViewEvent.Back)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, SplitFragment.newInstance()
            ).commit()
        }

        startOver.setOnClickListener {
            viewModel.onEvent(ResultViewEvent.StartOver)
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayTotal(num: Double) {
        total.text = num.toString()
    }

    private fun setTipState(bool: Boolean) {
        if (bool) {
            currencyTipGroup.visibility = View.VISIBLE
            percentageTipGroup.visibility = View.GONE
            currencyButton.isEnabled = false
            percentageButton.isEnabled = true
            currencyTip.setText(viewModel.getTip()?.toString() ?: "")
        } else {
            currencyTipGroup.visibility = View.GONE
            percentageTipGroup.visibility = View.VISIBLE
            currencyButton.isEnabled = true
            percentageButton.isEnabled = false
            percentageTip.setText(viewModel.getTip()?.div(viewModel.getSubtotal()!!)?.toString() ?: "")
        }
    }
}