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
import com.example.divvie.*
import com.example.divvie.database.Person
import com.example.divvie.viewModels.BowlsViewModel

class BowlsFragment : Fragment() {
    companion object {
        fun newInstance() = BowlsFragment()
    }
    private var viewModel =
        BowlsViewModel(activity!!.application, BowlsViewState())
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
        viewModel = ViewModelProviders.of(activity!!).get(BowlsViewModel::class.java)
        viewModel.onEvent(BowlsViewEvent.DisplayBowls)
        viewModel.viewStateObservable.observeForever { render(it) }
    }

    private fun changeColor(view: View, color: Int) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val currency: TextView = view.findViewById(R.id.currency)
        val priceAmount: TextView = view.findViewById(R.id.price_amount)
        currency.setTextColor(color)
        priceAmount.setTextColor(color)
        image.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private fun render(viewState: BowlsViewState) {
        for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
            val view = bowlsList.getChildAt(i)
            if (i < viewState.numberOfBowls) {
                view.visibility = View.VISIBLE
            }
            else {
                view.visibility = View.GONE
            }
        }
    }
}