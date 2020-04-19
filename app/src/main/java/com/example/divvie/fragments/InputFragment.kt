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
import com.example.divvie.database.Person

class InputFragment : Fragment() {
    companion object {
        fun newInstance() = InputFragment()
    }

    private lateinit var viewModel: DivvieViewModel
    private lateinit var numberOfPeopleText: TextView
    private lateinit var upButton: ImageButton
    private lateinit var downButton: ImageButton
    private lateinit var editSubtotalText: EditText
    private lateinit var editTaxText: EditText
    private lateinit var nextButton: Button
    private var numberOfPeople = NUMBER_OF_PEOPLE_DEFAULT

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

        nextButton.isEnabled = false

        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.setDisplayPrices(false)
        for (i in 0 until numberOfPeople) {
            viewModel.insertPerson(Person(id = i))
        }
        viewModel.setSubtotal(AMOUNT_DEFAULT)
        viewModel.setTax(AMOUNT_DEFAULT)
        viewModel.getNumberOfPeople().observe(viewLifecycleOwner, Observer { displayNumberOfPeople(it) })

        upButton.setOnClickListener {
            var num = numberOfPeopleText.text.toString().toInt()
            if (num < MAX_NUMBER_OF_PEOPLE) {
                num += 1
                viewModel.insertPerson(Person(id = num - 1))
            }
        }

        downButton.setOnClickListener {
            var num = numberOfPeopleText.text.toString().toInt()
            if (num > MIN_NUMBER_OF_PEOPLE) {
                num -= 1
                viewModel.deletePerson(Person(id = num))
            }
        }

        editSubtotalText.addTextChangedListener(object: TextWatcher {
            // TODO remind user it's pretax
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val num = editSubtotalText.text.toString()
                if (num != "") {
                    nextButton.isEnabled = true
                    viewModel.setSubtotal(num.toDouble())
                } else {
                    nextButton.isEnabled = false
                    viewModel.setSubtotal(AMOUNT_DEFAULT)
                    // TODO show user this cannot be 0
                }
            }
        })

        editTaxText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val num = editTaxText.text.toString()
                if (num != "") {
                    viewModel.setTax(num.toDouble())
                } else {
                    viewModel.setTax(AMOUNT_DEFAULT)
                }
            }
        })

        nextButton.setOnClickListener {
            editSubtotalText.isEnabled = false
            editTaxText.isEnabled = false
            viewModel.setDisplayPrices(true)
            viewModel.splitPretaxEqually()
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout,
                SplitFragment.newInstance()
            )
                .commit()
        }
    }

    private fun displayNumberOfPeople(num: Int) {
        numberOfPeople = num
        numberOfPeopleText.text = num.toString()
    }
}