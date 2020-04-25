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
import com.example.divvie.BowlsViewEvent
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
        viewModel.onEvent(BowlsViewEvent.DisplayFragment)
        viewModel.getAllPerson().observe(viewLifecycleOwner, Observer { displayBowlState(it) })
        viewModel.selectPersonObservable.observe(viewLifecycleOwner, Observer { clickableBowls(it) })
    }

    private fun changeColor(view: View, color: Int) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val currency: TextView = view.findViewById(R.id.currency)
        val priceAmount: TextView = view.findViewById(R.id.price_amount)
        currency.setTextColor(color)
        priceAmount.setTextColor(color)
        image.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private fun displayBowlState(list: List<Person>) {
        //TODO but where bowls > 4 will display previous tax + tip before last crash
        for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
            val view = bowlsList.getChildAt(i)
            if (i < list.size) {
                val person = list[i]
                val priceAmount: TextView = view.findViewById(R.id.price_amount)
                val price: LinearLayout = view.findViewById(R.id.price)
                val personalSubtotal = person.subtotal
                if (personalSubtotal == null) {
                    price.visibility = View.GONE
                } else {
                    price.visibility = View.VISIBLE
                    val personalTax = person.tax ?: 0.0
                    val personalTip = person.tip ?: 0.0
                    val personalTempPrice = person.tempPrice ?: 0.0
                    val personalTotal =
                        personalSubtotal + personalTax + personalTip + personalTempPrice
                    priceAmount.text = personalTotal.toString()
                }
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }

    private fun clickableBowls(bool: Boolean) {
        if (bool) {
            viewModel.tempItemObservable.observe(viewLifecycleOwner, Observer { split(it.listOfIndex) })
            for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
                val view = bowlsList.getChildAt(i)
                changeColor(view, Color.DKGRAY)
                view.isClickable = true
                view.setOnClickListener {
                    viewModel.onEvent(BowlsViewEvent.ClickBowl(i))
                }
            }
        } else {
            viewModel.tempItemObservable.removeObservers(viewLifecycleOwner)
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
        }
    }
}