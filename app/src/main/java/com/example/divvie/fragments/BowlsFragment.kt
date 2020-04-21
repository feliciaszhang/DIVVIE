package com.example.divvie.fragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.divvie.MAX_NUMBER_OF_PEOPLE
import com.example.divvie.R
import com.example.divvie.DivvieViewModel
import com.example.divvie.data.Person

class BowlsFragment : Fragment() {
    companion object {
        fun newInstance() = BowlsFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var bowlsList: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.bowls_fragment, container, false)
        bowlsList = fragment.findViewById(R.id.bowls)
        for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
            changeColor(bowlsList.getChildAt(i), Color.LTGRAY)
        }
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.getNumberOfPeople().observe(viewLifecycleOwner, Observer { displayBowls(it) })
        viewModel.getAllPerson().observe(viewLifecycleOwner, Observer { updatePrices(it) })
        viewModel.displayPricesObservable.observe(viewLifecycleOwner, Observer { displayPrices(it) })
        viewModel.selectPersonObservable.observe(viewLifecycleOwner, Observer { clickableBowls(it) })
        viewModel.selectedPersonListObservable.observe(viewLifecycleOwner, Observer { split(it) })
    }

    private fun changeColor(view: View, color: Int) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val currency: TextView = view.findViewById(R.id.currency)
        val priceAmount: TextView = view.findViewById(R.id.price_amount)
        currency.setTextColor(color)
        priceAmount.setTextColor(color)
        image.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private fun displayBowls(num: Int) {
        for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
            val view = bowlsList.getChildAt(i)
            if (i < num) {
                view.visibility = View.VISIBLE
            }
            else {
                view.visibility = View.GONE
            }
        }
    }

    private fun displayPrices(bool: Boolean) {
        if (bool) {
            for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
                val view = bowlsList.getChildAt(i)
                val price: LinearLayout = view.findViewById(R.id.price)
                price.visibility = View.VISIBLE
            }
        }
    }

    private fun updatePrices(list: List<Person>) {
        for (i in list.indices) {
            val person = list[i]
            val view = bowlsList.getChildAt(i)
            val priceAmount: TextView = view.findViewById(R.id.price_amount)
            val personalTotal = person.subtotal + person.tax + person.tip
            priceAmount.text = personalTotal.toString()
        }
    }

    private fun clickableBowls(bool: Boolean) {
        if (bool) {
            viewModel.resetListOfSelected()
            for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
                val view = bowlsList.getChildAt(i)
                changeColor(view, Color.DKGRAY)
                view.isClickable = true
                view.setOnClickListener {
                    viewModel.alterListOfSelected(i)
                }
            }
        } else {
            for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
                val view = bowlsList.getChildAt(i)
                changeColor(view, Color.LTGRAY)
                view.isClickable = false
            }
        }
    }

    private fun split(listOfIndex: ArrayList<Int>) {
        for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
            val view = bowlsList.getChildAt(i)
            if (listOfIndex.contains(i)) {
                changeColor(view, Color.WHITE)
            } else {
                changeColor(view,Color.DKGRAY)
            }
            viewModel.split(i)
        }
    }
}