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
import com.example.divvie.NUMBER_OF_PEOPLE_DEFAULT
import com.example.divvie.database.Person
import kotlinx.android.synthetic.main.bowl.*

class BowlsFragment : Fragment() {
    companion object {
        fun newInstance() = BowlsFragment()
    }

    private lateinit var viewModel: DivvieViewModel
    private lateinit var bowlsList: LinearLayout
    private var numberOfBowls: Int = NUMBER_OF_PEOPLE_DEFAULT

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.bowls_fragment, container, false)
        bowlsList = fragment.findViewById(R.id.bowls)
        for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
            val view = bowlsList.getChildAt(i)
            val image: ImageView = view.findViewById(R.id.imageView)
            changeColor(image, Color.LTGRAY)
        }

        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.getNumberOfPeople().observe(viewLifecycleOwner, Observer { displayBowls(it) })
        viewModel.getAllPerson().observe(viewLifecycleOwner, Observer { updateBowls(it) })
        viewModel.displayPricesObservable.observe(viewLifecycleOwner, Observer { displayPrices(it) })
        viewModel.selectPersonObservable.observe(viewLifecycleOwner, Observer { greyoutBowls(it) })
    }

    fun changeColor(image: ImageView, color: Int) {
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
        numberOfBowls = num
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

    private fun updateBowls(list: List<Person>) {
        for (i in list.indices) {
            val person = list[i]
            val view = bowlsList.getChildAt(i)
            val priceAmount: TextView = view.findViewById(R.id.price_amount)
            val personalTotal = person.subtotal + person.tax + person.tip
            priceAmount.text = personalTotal.toString()
        }
    }

    private fun greyoutBowls(bool: Boolean) {
        if (bool) {
            for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
                val view = bowlsList.getChildAt(i)
                view.isClickable = true
                val image: ImageView = view.findViewById(R.id.imageView)
                val currency: TextView = view.findViewById(R.id.currency)
                val priceAmount: TextView = view.findViewById(R.id.price_amount)
                currency.setTextColor(Color.DKGRAY)
                priceAmount.setTextColor(Color.DKGRAY)
                changeColor(image, Color.DKGRAY)
            }
        }
    }
}