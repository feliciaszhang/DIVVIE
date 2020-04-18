package com.example.divvie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.divvie.AMOUNT_DEFAULT
import com.example.divvie.DivvieViewModel
import com.example.divvie.R

class ResultFragment : Fragment() {
    companion object {
        fun newInstance() = ResultFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var subtotal: TextView
    private lateinit var tax: TextView
    private lateinit var tip: EditText
    private lateinit var total: TextView
    private lateinit var start_over: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.result_fragment, container, false)
        subtotal = fragment.findViewById(R.id.subtotal_amount)
        tax = fragment.findViewById(R.id.tax_amount)
        tip = fragment.findViewById(R.id.edit_tip)
        total = fragment.findViewById(R.id.total_amount)
        start_over = fragment.findViewById(R.id.start_over)

        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.setTip(AMOUNT_DEFAULT)
        viewModel.subtotalObservable.observe(viewLifecycleOwner, Observer { displaySubtotal(it) })
        viewModel.taxObservable.observe(viewLifecycleOwner, Observer{  displayTax(it) })
        viewModel.tipObservable.observe(viewLifecycleOwner, Observer{  displayTip(it) })
        viewModel.totalObservable.observe(viewLifecycleOwner, Observer{  displayTotal(it) })

        tip.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (tip.text.toString() != "") {
                    val num = tip.text.toString().toDouble()
                    viewModel.setTip(num)
                }
            }
        }
    }

    private fun displaySubtotal(num: Double) {
        subtotal.text = num.toString()
    }

    private fun displayTax(num: Double) {
        tax.text = num.toString()
    }

    private fun displayTip(num: Double) {
        tip.hint = num.toString()
    }

    private fun displayTotal(num: Double) {
        total.text = num.toString()
    }
}