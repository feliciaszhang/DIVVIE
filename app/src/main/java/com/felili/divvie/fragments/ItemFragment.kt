package com.felili.divvie.fragments

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
import com.felili.divvie.*
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

        editItemText.onFocusChangeListener = (View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = editItemText.text.toString()
                val cleanedText = try {
                    filter.clean(text)
                } catch (e: Exception) {
                    text
                }
                editItemText.setText(cleanedText)
            }
        })

        nextButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ItemNext) }

        undoButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ItemUndo) }

        clearAllButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ItemClear) }

        backButton.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, SplitFragment.newInstance()
            ).commit()
        }
    }

    private fun render(viewState: DivvieViewState) {
        val tempLeftover = (viewState.leftover ?: BigDecimal.ZERO) - (viewState.tempItemPrice ?: BigDecimal.ZERO)
        if (tempLeftover == BigDecimal.ZERO || tempLeftover.toString() == "0.00") {
            doneButton.setOnClickListener {
                viewModel.onEvent(DivvieViewEvent.ItemDone)
                fragmentManager!!.beginTransaction().replace(
                    R.id.info_fragment_layout, CalculateFragment.newInstance()
                ).commit()
            }
        } else {
            doneButton.setOnClickListener { viewModel.onEvent(DivvieViewEvent.ItemDone) }
        }
        doneButton.isEnabled = viewState.tempItemListOfIndex.size != 0
        nextButton.isEnabled = viewState.tempItemPrice != BigDecimal.ZERO
                && viewState.tempItemPrice.toString() != "0.00"
                && viewState.tempItemPrice != null
                && !viewState.invalidItem
                && tempLeftover >= BigDecimal.ZERO
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
            editItemText.background = editTextBackground
            itemHelper.text = String.format(resources.getString(R.string.leftover), filter.clean(tempLeftover.toPlainString()))
            nextButton.visibility = View.VISIBLE
            doneButton.visibility = View.GONE
            if (!viewState.isItemEditing) {
                editItemText.setText("")
            }
        }
        if (viewState.tempItemPrice.toString() == "0.00") {
            itemHelper.text = resources.getString(R.string.zero)
            itemHelper.setTextColor(resources.getColor(R.color.colorAccent, context!!.theme))
            editItemText.background.colorFilter = PorterDuffColorFilter(
                resources.getColor(R.color.colorAccent, context!!.theme), PorterDuff.Mode.SRC_ATOP)
        }
        if (viewState.invalidItem) {
            itemHelper.text = resources.getString(R.string.warning)
            itemHelper.setTextColor(resources.getColor(R.color.colorAccent, context!!.theme))
            editItemText.background.colorFilter = PorterDuffColorFilter(
                resources.getColor(R.color.colorAccent, context!!.theme), PorterDuff.Mode.SRC_ATOP)
        }
        if (!viewState.invalidItem && !viewState.isSplittingBowls && viewState.tempItemPrice.toString() != "0.00") {
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