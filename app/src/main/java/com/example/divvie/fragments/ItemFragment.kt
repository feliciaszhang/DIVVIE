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
import com.example.divvie.database.Item
import java.util.Stack

class ItemFragment : Fragment() {
    companion object {
        fun newInstance() = ItemFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var editItemText: EditText
    private lateinit var leftoverText: TextView
    private lateinit var nextButton: Button
    private lateinit var backButton: Button
    private lateinit var clearAllButton: Button
    private val itemStack: Stack<Item> = Stack()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.item_fragment, container, false)
        editItemText = fragment.findViewById(R.id.edit_item)
        leftoverText = fragment.findViewById(R.id.leftover)
        nextButton = fragment.findViewById(R.id.next)
        backButton = fragment.findViewById(R.id.back)
        clearAllButton = fragment.findViewById(R.id.clear_all)

        nextButton.isEnabled = false

        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        val subtotal = viewModel.getSubtotal()
        leftoverText.text = String.format(resources.getString(R.string.leftover), subtotal.toString())
        viewModel.setEnterPrice(true)
        viewModel.enterPriceObservable.observe(viewLifecycleOwner, Observer { enableEnterPrice(it) })
        viewModel.currentItemPriceObservable.observe(viewLifecycleOwner, Observer { calculateLeftover(subtotal, it) })

        editItemText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val num = editItemText.text.toString()
                if (num != "") {
                    nextButton.isEnabled = true
                    viewModel.setCurrentItemPrice(num.toDouble())
                } else {
                    nextButton.isEnabled = false
                    viewModel.setCurrentItemPrice(AMOUNT_DEFAULT)
                }
            }
        })

        nextButton.setOnClickListener {
            viewModel.setEnterPrice(false)
        }
    }

    private fun enableEnterPrice(bool: Boolean) {

    }

    private fun calculateLeftover(subtotal: Double?, num: Double) {
        if (subtotal != null) {
            val leftover: String = (subtotal - num).toString()
            leftoverText.text = String.format(resources.getString(R.string.leftover), leftover)
        }
    }
}