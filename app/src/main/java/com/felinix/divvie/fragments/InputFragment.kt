package com.felinix.divvie.fragments

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.felinix.divvie.*
import java.math.BigDecimal

class InputFragment : Fragment() {
    companion object {
        fun newInstance() = InputFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var guestsText: TextView
    private lateinit var upButton: Button
    private lateinit var downButton: Button
    private lateinit var editSubtotalText: DivvieEditText
    private lateinit var editTaxText: DivvieEditText
    private lateinit var nextButton: Button
    private lateinit var subtotalHelper: TextView
    private lateinit var taxHelper: TextView
    private val filter = CurrencyInputFilter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.input_fragment, container, false)
        guestsText = fragment.findViewById(R.id.guests)
        upButton = fragment.findViewById(R.id.up_button)
        downButton = fragment.findViewById(R.id.down_button)
        editSubtotalText = fragment.findViewById(R.id.edit_subtotal)
        editTaxText = fragment.findViewById(R.id.edit_tax)
        nextButton = fragment.findViewById(R.id.next)
        subtotalHelper = fragment.findViewById(R.id.subtotal_helper)
        taxHelper = fragment.findViewById(R.id.tax_helper)
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(DivvieViewEvent.DisplayInputFragment)
        viewModel.viewStateObservable.observe(viewLifecycleOwner, Observer { render(it) })

        editSubtotalText.filters = arrayOf(filter)
        editTaxText.filters = arrayOf(filter)

        upButton.setOnClickListener {
            upButton.requestFocusFromTouch()
            viewModel.onEvent(DivvieViewEvent.InputInsertPerson)
        }

        downButton.setOnClickListener {
            downButton.requestFocusFromTouch()
            viewModel.onEvent(DivvieViewEvent.InputRemovePerson)
        }

        editSubtotalText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = editSubtotalText.text.toString()
                editSubtotalText.textSize = SizeCalculator(42f).resize(text)
                try {
                    filter.clean(text)
                    viewModel.onEvent(DivvieViewEvent.InputEnterSubtotal("0" + text))
                } catch (e: java.lang.Exception) {
                    viewModel.onEvent(DivvieViewEvent.InvalidSubtotal)
                }
            }
        })

        editTaxText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = editTaxText.text.toString()
                editTaxText.textSize = SizeCalculator(42f).resize(text)
                try {
                    filter.clean(text)
                    viewModel.onEvent(DivvieViewEvent.InputEnterTax("0" + text))
                } catch (e: java.lang.Exception) {
                    viewModel.onEvent(DivvieViewEvent.InvalidTax)
                }
            }
        })

        editSubtotalText.onFocusChangeListener = (View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = editSubtotalText.text.toString()
                val cleanedText = try {
                    filter.clean(text)
                } catch (e: Exception) {
                    text
                }
                editSubtotalText.setText(cleanedText)
            }
        })

        editTaxText.onFocusChangeListener = (View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = editTaxText.text.toString()
                val cleanedText = try {
                    filter.clean(text)
                } catch (e: java.lang.Exception) {
                    text
                }
                editTaxText.setText(cleanedText)
            }
        })

        editTaxText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == IME_ACTION_DONE && nextButton.isEnabled) {
                next()
                true
            } else {
                false
            }
        }

        nextButton.setOnClickListener { next() }
    }

    private fun next() {
        viewModel.onEvent(DivvieViewEvent.InputToSplit)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editTaxText.windowToken, 0)
        fragmentManager!!.beginTransaction().replace(
            R.id.info_fragment_layout,
            SplitFragment.newInstance()
        ).commit()
    }

    private fun render(viewState: DivvieViewState) {
        guestsText.text = viewState.personList.size.toString()
        nextButton.isEnabled = viewState.subtotal != BigDecimal.ZERO
                && viewState.subtotal != null
                && !viewState.invalidSubtotal
                && !viewState.invalidTax
        if (viewState.subtotal != null && !viewState.isSubtotalEditing) {
            editSubtotalText.setText(filter.clean(viewState.subtotal.toPlainString()))
        }
        if (viewState.tax != null && !viewState.isTaxEditing) {
            editTaxText.setText(filter.clean(viewState.tax.toPlainString()))
        }
        if (viewState.invalidSubtotal) {
            subtotalHelper.text = resources.getString(R.string.warning)
            subtotalHelper.setTextColor(resources.getColor(R.color.colorAccent, context!!.theme))
            editSubtotalText.background.colorFilter = PorterDuffColorFilter(
                resources.getColor(R.color.colorAccent, context!!.theme), PorterDuff.Mode.SRC_ATOP)
        }
        if (viewState.invalidTax) {
            taxHelper.visibility = View.VISIBLE
            taxHelper.setTextColor(resources.getColor(R.color.colorAccent, context!!.theme))
            editTaxText.background.colorFilter = PorterDuffColorFilter(
                resources.getColor(R.color.colorAccent, context!!.theme), PorterDuff.Mode.SRC_ATOP)
        }
        if (!viewState.invalidSubtotal){
            subtotalHelper.text = resources.getString(R.string.subtotal_helper)
            subtotalHelper.setTextColor(resources.getColor(R.color.colorWhite, context!!.theme))
            editSubtotalText.background.colorFilter = PorterDuffColorFilter(
                resources.getColor(R.color.colorLight, context!!.theme), PorterDuff.Mode.SRC_ATOP)
        }
        if (!viewState.invalidTax) {
            taxHelper.visibility = View.GONE
            editTaxText.background.colorFilter = PorterDuffColorFilter(
                resources.getColor(R.color.colorLight, context!!.theme), PorterDuff.Mode.SRC_ATOP)
        }
    }
}