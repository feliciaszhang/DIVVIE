package com.example.divvie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.divvie.MAX_NUMBER_OF_PEOPLE
import com.example.divvie.R
import com.example.divvie.DivvieViewModel
import kotlinx.android.synthetic.main.bowl.view.*

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

        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.getNumberOfPeople().observe(viewLifecycleOwner, Observer { displayBowls(it) })
        viewModel.displayPricesObservable.observe(viewLifecycleOwner, Observer { displayPrices(it) })
    }

    private fun displayBowls(num: Int) {
        for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
            if (i < num) {
                val view = bowlsList.getChildAt(i)
                view.visibility = View.VISIBLE
                val price: TextView = view.findViewById(R.id.price)
                price.text = num.toString()
            }
            else {
                bowlsList.getChildAt(i).visibility = View.GONE
            }
        }
    }

    private fun displayPrices(bool: Boolean) {
        if (bool) {
            for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
                val view = bowlsList.getChildAt(i)
                val price: TextView = view.findViewById(R.id.price)
                price.visibility = View.VISIBLE
            }
        }
    }
}