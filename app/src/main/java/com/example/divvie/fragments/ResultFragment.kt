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
    private lateinit var startOver: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.result_fragment, container, false)
        subtotal = fragment.findViewById(R.id.subtotal_amount)
        tax = fragment.findViewById(R.id.tax_amount)
        tip = fragment.findViewById(R.id.edit_tip)
        total = fragment.findViewById(R.id.total_amount)
        startOver = fragment.findViewById(R.id.start_over)
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.totalObservable.observe(viewLifecycleOwner, Observer{  displayTotal(it) })

        subtotal.text = viewModel.getSubtotal().toString()

        tax.text = viewModel.getTax().toString()

        tip.hint = AMOUNT_DEFAULT.toString()

        tip.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val num = tip.text.toString()
                if (num != "") {
                    viewModel.setTip(num.toDouble())
                } else {
                    viewModel.setTip(AMOUNT_DEFAULT)
                }
                viewModel.calculatePersonResult()
            }
        })
    }

    private fun displayTotal(num: Double) {
        total.text = num.toString()
    }
}