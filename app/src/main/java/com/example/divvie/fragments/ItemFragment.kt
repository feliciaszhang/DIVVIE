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
import com.example.divvie.R
import com.example.divvie.DivvieViewModel
import com.example.divvie.ItemViewEvent

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
    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.onEvent(ItemViewEvent.EnterItemPrice(editItemText.text.toString()))
        }
    }

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
        viewModel.onEvent(ItemViewEvent.DisplayFragment)
        viewModel.tempItemObservable.observe(viewLifecycleOwner, Observer { setTemp(it.basePrice, it.listOfIndex) })
        viewModel.selectPersonObservable.observe(viewLifecycleOwner, Observer { disableViews(it) })
        viewModel.leftoverObservable.observe(viewLifecycleOwner, Observer { splitComplete(it) })
        viewModel.itemStackObservable.observe(viewLifecycleOwner, Observer { undoOrBack(it.size) })

        nextButton.setOnClickListener {
            viewModel.onEvent(ItemViewEvent.Next)
        }

        doneButton.setOnClickListener {
            viewModel.onEvent(ItemViewEvent.Done)
        }

        undoButton.setOnClickListener {
            viewModel.onEvent(ItemViewEvent.Undo)
        }

        backButton.setOnClickListener {
            viewModel.onEvent(ItemViewEvent.Back)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, SplitFragment.newInstance()
            ).commit()
        }
    }

    private fun undoOrBack(stackSize: Int) {
        if (stackSize > 0) {
            undoButton.visibility = View.VISIBLE
            backButton.visibility = View.GONE
        } else {
            undoButton.visibility = View.GONE
            backButton.visibility = View.VISIBLE
        }
    }

    private fun setTemp(num: Double, listOfIndex: ArrayList<Int>) {
        doneButton.isEnabled = listOfIndex.size != 0
        nextButton.isEnabled = num != 0.0
        val leftover = viewModel.getLeftover()!! - num
        leftoverText.text = String.format(resources.getString(R.string.leftover), leftover.toString())
        if (leftover < 0) {
            nextButton.isEnabled = false
            // TODO show user this cannot be negative
        }
    }

    private fun disableViews(bool: Boolean) {
        if (bool) {
            editItemText.visibility = View.GONE
            editItemText.removeTextChangedListener(textWatcher)
            leftoverText.visibility = View.GONE
            nextButton.visibility = View.GONE
            itemText.visibility = View.VISIBLE
            tap.visibility = View.VISIBLE
            doneButton.visibility = View.VISIBLE
            itemText.text = viewModel.getTempItem()!!.basePrice.toString()
        } else {
            editItemText.visibility = View.VISIBLE
            editItemText.text.clear()
            editItemText.addTextChangedListener(textWatcher)
            leftoverText.visibility = View.VISIBLE
            nextButton.visibility = View.VISIBLE
            itemText.visibility = View.GONE
            tap.visibility = View.GONE
            doneButton.visibility = View.GONE
        }
    }

    private fun splitComplete(leftover: Double) {
        if (leftover == 0.0) {
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, SplitFragment.newInstance()
            ).commit()
        }
    }
}