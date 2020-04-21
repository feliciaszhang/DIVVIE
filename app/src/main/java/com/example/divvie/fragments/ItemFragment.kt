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
import com.example.divvie.R
import com.example.divvie.DivvieViewModel

class ItemFragment : Fragment() {
    companion object {
        fun newInstance() = ItemFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var editItemText: EditText
    private lateinit var itemText: TextView
    private lateinit var leftoverText: TextView
    private lateinit var tap: TextView
    private lateinit var nextButton: Button
    private lateinit var doneButton: Button
    private lateinit var backButton: Button
    private lateinit var undoButton: Button
    private lateinit var clearAllButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.item_fragment, container, false)
        editItemText = fragment.findViewById(R.id.edit_item)
        itemText = fragment.findViewById(R.id.item_text)
        leftoverText = fragment.findViewById(R.id.leftover)
        tap = fragment.findViewById(R.id.tap)
        nextButton = fragment.findViewById(R.id.next)
        doneButton = fragment.findViewById(R.id.done)
        backButton = fragment.findViewById(R.id.back)
        undoButton = fragment.findViewById(R.id.undo)
        clearAllButton = fragment.findViewById(R.id.clear_all)
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        val subtotal = viewModel.getSubtotal()
        leftoverText.text = String.format(resources.getString(R.string.leftover), viewModel.getSubtotal().toString())
        viewModel.tempItemPriceObservable.observe(viewLifecycleOwner, Observer { calculateLeftover(subtotal, it) })
        viewModel.selectPersonObservable.observe(viewLifecycleOwner, Observer { disableViews(it) })
        viewModel.selectedPersonListObservable.observe(viewLifecycleOwner, Observer { doneSelectingPerson(it) })

        editItemText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val num = editItemText.text.toString()
                if (num != "") {
                    viewModel.setTempItemPrice(num.toDouble())
                } else {
                    viewModel.setTempItemPrice(AMOUNT_DEFAULT)
                }
            }
        })

        nextButton.setOnClickListener {
            viewModel.setSelectPerson(true)
        }

        doneButton.setOnClickListener {
            viewModel.setSelectPerson(false)
            viewModel.commitItem()
        }
    }

    private fun calculateLeftover(subtotal: Double?, num: Double) {
        nextButton.isEnabled = num != AMOUNT_DEFAULT
        if (subtotal != null) {
            val leftover: Double = subtotal - num
            leftoverText.text = String.format(resources.getString(R.string.leftover), leftover.toString())
            if (leftover < 0) {
                nextButton.isEnabled = false
            }
            // TODO show user this cannot be negative
        }
    }

    private fun disableViews(bool: Boolean) {
        if (bool) {
            editItemText.visibility = View.GONE
            leftoverText.visibility = View.GONE
            nextButton.visibility = View.GONE
            itemText.visibility = View.VISIBLE
            tap.visibility = View.VISIBLE
            doneButton.visibility = View.VISIBLE
            itemText.text = viewModel.getTempItemPrice().toString()
        } else {
            editItemText.visibility = View.VISIBLE
            leftoverText.visibility = View.VISIBLE
            nextButton.visibility = View.VISIBLE
            itemText.visibility = View.GONE
            tap.visibility = View.GONE
            doneButton.visibility = View.GONE
            editItemText.text.clear()
        }
    }

    private fun doneSelectingPerson(listOfIndex: ArrayList<Int>) {
        doneButton.isEnabled = listOfIndex.size != 0
    }
}