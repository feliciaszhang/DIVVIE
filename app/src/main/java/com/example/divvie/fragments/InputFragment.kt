package com.example.divvie.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.divvie.*
import com.example.divvie.data.Person

class InputFragment : Fragment() {
    companion object {
        fun newInstance() = InputFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var numberOfPeopleText: TextView
    private lateinit var upButton: Button
    private lateinit var downButton: Button
    private lateinit var editSubtotalText: EditText
    private lateinit var editTaxText: EditText
    private lateinit var nextButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.input_fragment, container, false)
        numberOfPeopleText = fragment.findViewById(R.id.number_of_people)
        upButton = fragment.findViewById(R.id.up_button)
        downButton = fragment.findViewById(R.id.down_button)
        editSubtotalText = fragment.findViewById(R.id.edit_subtotal)
        editTaxText = fragment.findViewById(R.id.edit_tax)
        nextButton = fragment.findViewById(R.id.next)
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(InputViewEvent.DisplayFragment)
        viewModel.viewStateObservable.observe(viewLifecycleOwner, Observer { render(it) })

        upButton.setOnClickListener { viewModel.onEvent(InputViewEvent.InsertPerson) }

        downButton.setOnClickListener { viewModel.onEvent(InputViewEvent.RemovePerson) }

        editSubtotalText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEvent(InputViewEvent.EnterSubtotal(editSubtotalText.text.toString()))
            }
        })

        editTaxText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEvent(InputViewEvent.EnterTax(editTaxText.text.toString()))
            }
        })

        nextButton.setOnClickListener {
            viewModel.onEvent(InputViewEvent.Next)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout,
                SplitFragment.newInstance()
            ).commit()
        }
    }

    private fun render(viewState: DivvieViewState) {
        numberOfPeopleText.text = viewState.personList.size.toString()
        nextButton.isEnabled = viewState.subtotal != 0.0 && viewState.subtotal != null
        if (viewState.subtotal != null && !viewState.isSubtotalEditing) {
            editSubtotalText.setText(viewState.subtotal.toString())
        }
        if (viewState.tax != null && !viewState.isTaxEditing) {
            editTaxText.setText(viewState.tax.toString())
        }
    }
}