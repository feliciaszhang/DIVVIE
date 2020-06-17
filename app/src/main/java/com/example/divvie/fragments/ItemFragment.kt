package com.example.divvie.fragments

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.divvie.*
import java.math.BigDecimal

class ItemFragment : Fragment() {
    companion object {
        fun newInstance() = ItemFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var editItemText: EditText
    private lateinit var itemHelper: TextView
    private lateinit var nextButton: Button
    private lateinit var doneButton: Button
    private lateinit var backButton: Button
    private lateinit var undoButton: Button
    private lateinit var clearAllButton: Button
    private lateinit var editTextBackground: Drawable
    private val filter = CurrencyInputFilter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.item_fragment, container, false)
        editItemText = fragment.findViewById(R.id.edit_item)
        itemHelper = fragment.findViewById(R.id.item_helper)
        nextButton = fragment.findViewById(R.id.next)
        doneButton = fragment.findViewById(R.id.done)
        backButton = fragment.findViewById(R.id.back)
        undoButton = fragment.findViewById(R.id.undo)
        clearAllButton = fragment.findViewById(R.id.clear_all)
        editTextBackground = editItemText.background
        return fragment
    }

    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = editItemText.text.toString()
            editItemText.textSize = SizeCalculator(42f).resize(text)
            try {
                filter.clean(text)
                viewModel.onEvent(DivvieViewEvent.ItemEnterPrice("0" + text))
            } catch (e: java.lang.Exception) {
                viewModel.onEvent(DivvieViewEvent.InvalidItem)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(DivvieViewEvent.DisplayItemFragment)
        viewModel.viewStateObservable.observe(viewLifecycleOwner, Observer { render(it) })

        editItemText.filters = arrayOf(filter)

        editItemText.addTextChangedListener(textWatcher)

        editItemText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE && nextButton.isEnabled) {
                viewModel.onEvent(DivvieViewEvent.ItemNext)
                true
            } else {
                false
            }
        }

        nextButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ItemNext) }

        doneButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ItemDone) }

        undoButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ItemUndo) }

        clearAllButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ItemClear) }

        backButton.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, SplitFragment.newInstance()
            ).commit()
        }
    }

    private fun render(viewState: DivvieViewState) {
        if (viewState.leftover == BigDecimal.ZERO) {
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, CalculateFragment.newInstance()
            ).commit()
        }
        val tempLeftover = (viewState.leftover ?: BigDecimal.ZERO) - (viewState.tempItemPrice ?: BigDecimal.ZERO)
        doneButton.isEnabled = viewState.tempItemListOfIndex.size != 0
        nextButton.isEnabled = viewState.tempItemPrice != BigDecimal.ZERO && !viewState.invalidItem && tempLeftover >= BigDecimal.ZERO
        clearAllButton.isEnabled = viewState.itemList.size > 0
        editItemText.isEnabled = !viewState.isSplittingBowls
        if (viewState.itemList.size > 0 || (viewState.tempItemPrice != BigDecimal.ZERO && viewState.isSplittingBowls)) {
            undoButton.visibility = View.VISIBLE
            backButton.visibility = View.GONE
        } else {
            undoButton.visibility = View.GONE
            backButton.visibility = View.VISIBLE
        }
        if (viewState.isSplittingBowls) {
            editItemText.background = null
            editItemText.removeTextChangedListener(textWatcher)
            editItemText.setText(filter.clean(viewState.tempItemPrice?.toPlainString() ?: ""))
            editItemText.addTextChangedListener(textWatcher)
            itemHelper.text = resources.getString(R.string.tap)
            nextButton.visibility = View.GONE
            doneButton.visibility = View.VISIBLE
        } else {
            editItemText.requestFocus()
            editItemText.background = editTextBackground
            itemHelper.text = String.format(resources.getString(R.string.leftover), filter.clean(tempLeftover.toPlainString()))
            nextButton.visibility = View.VISIBLE
            doneButton.visibility = View.GONE
            if (!viewState.isItemEditing) {
                editItemText.setText("")
            }
        }
        if (viewState.invalidItem) {
            itemHelper.text = resources.getString(R.string.warning)
            itemHelper.setTextColor(resources.getColor(R.color.colorAccent, context!!.theme))
            editItemText.background.colorFilter = PorterDuffColorFilter(
                resources.getColor(R.color.colorAccent, context!!.theme), PorterDuff.Mode.SRC_ATOP)
        }
        if (!viewState.invalidItem && !viewState.isSplittingBowls) {
            itemHelper.text = String.format(resources.getString(R.string.leftover), filter.clean(tempLeftover.toPlainString()))
            itemHelper.setTextColor(resources.getColor(R.color.colorWhite, context!!.theme))
            if (!viewState.isSplittingBowls) {
                editItemText.background.colorFilter = PorterDuffColorFilter(
                    resources.getColor(R.color.colorLight, context!!.theme),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }
    }
}