package com.example.divvie.fragments

import android.graphics.drawable.Drawable
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
import com.example.divvie.*

class ItemFragment : Fragment() {
    companion object {
        fun newInstance() = ItemFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var editItemText: EditText
    private lateinit var leftoverText: TextView
    private lateinit var tap: TextView
    private lateinit var nextButton: Button
    private lateinit var doneButton: Button
    private lateinit var backButton: Button
    private lateinit var undoButton: Button
    private lateinit var clearAllButton: Button
    private lateinit var editTextBackground: Drawable
    private val filter = DecimalDigitsInputFilter(2)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.item_fragment, container, false)
        editItemText = fragment.findViewById(R.id.edit_item)
        leftoverText = fragment.findViewById(R.id.leftover)
        tap = fragment.findViewById(R.id.tap)
        nextButton = fragment.findViewById(R.id.next)
        doneButton = fragment.findViewById(R.id.done)
        backButton = fragment.findViewById(R.id.back)
        undoButton = fragment.findViewById(R.id.undo)
        clearAllButton = fragment.findViewById(R.id.clear_all)
        editTextBackground = editItemText.background
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(ItemViewEvent.DisplayFragment)
        viewModel.viewStateObservable.observe(viewLifecycleOwner, Observer { render(it) })

        editItemText.filters = arrayOf(filter)

        editItemText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEvent(ItemViewEvent.EnterItemPrice(editItemText.text.toString()))
            }
        })

        nextButton.setOnClickListener { viewModel.onEvent(ItemViewEvent.Next) }

        doneButton.setOnClickListener { viewModel.onEvent(ItemViewEvent.Done) }

        undoButton.setOnClickListener { viewModel.onEvent(ItemViewEvent.Undo) }

        clearAllButton.setOnClickListener { viewModel.onEvent(ItemViewEvent.ClearAll) }

        backButton.setOnClickListener {
            viewModel.onEvent(ItemViewEvent.Back)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, SplitFragment.newInstance()
            ).commit()
        }
    }

    private fun render(viewState: DivvieViewState) {
        doneButton.isEnabled = viewState.tempItemListOfIndex.size != 0
        nextButton.isEnabled = viewState.tempItemBasePrice != 0.0
        clearAllButton.isEnabled = viewState.itemStack.size > 0
        editItemText.isEnabled = !viewState.isSplittingBowls
        if (viewState.itemStack.size > 0) {
            undoButton.visibility = View.VISIBLE
            backButton.visibility = View.GONE
        } else {
            undoButton.visibility = View.GONE
            backButton.visibility = View.VISIBLE
        }
        val tempLeftover = viewState.leftover!! - viewState.tempItemBasePrice
        leftoverText.text = String.format(resources.getString(R.string.leftover), tempLeftover.toString())
        if (tempLeftover < 0) {
            nextButton.isEnabled = false
        }
        if (viewState.leftover == 0.0) {
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, SplitFragment.newInstance()
            ).commit()
        }
        if (viewState.isSplittingBowls) {
            editItemText.background = null
            leftoverText.visibility = View.GONE
            nextButton.visibility = View.GONE
            tap.visibility = View.VISIBLE
            doneButton.visibility = View.VISIBLE
        } else {
            editItemText.requestFocus()
            editItemText.background = editTextBackground
            leftoverText.visibility = View.VISIBLE
            nextButton.visibility = View.VISIBLE
            tap.visibility = View.GONE
            doneButton.visibility = View.GONE
            if (!viewState.isItemEditing) {
                editItemText.setText("")
            }
        }
    }
}