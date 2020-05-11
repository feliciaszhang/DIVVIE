package com.example.divvie.fragments

import android.content.Intent
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
import com.example.divvie.data.Person
import java.io.Serializable

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
        for (i in 0 until MAX_GUESTS) {
            changeColor(bowlsList.getChildAt(i), Color.LTGRAY)
        }
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(BowlsViewEvent.DisplayFragment)
        viewModel.viewStateObservable.observe(viewLifecycleOwner, Observer { render(it) })
    }

    private fun changeColor(view: View, color: Int) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val currency: TextView = view.findViewById(R.id.currency)
        val priceAmount: TextView = view.findViewById(R.id.price_amount)
        currency.setTextColor(color)
        priceAmount.setTextColor(color)
        image.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private fun setVisibilityAttributes(i: Int, personList: Array<Person>, view: View) {
        val person = personList[i]
        val priceAmount: TextView = view.findViewById(R.id.price_amount)
        val price: LinearLayout = view.findViewById(R.id.price)
        val personalSub = person.subtotal
        if (personalSub != null) {
            price.visibility = View.VISIBLE
            val personalTax = person.tax ?: 0.0
            val personalTip = person.tip ?: 0.0
            val personalTempPrice = person.tempPrice ?: 0.0
            val total = personalSub + personalTax + personalTip + personalTempPrice
            priceAmount.text = total.toString()
        } else {
            price.visibility = View.GONE
        }
    }

    private fun render(viewState: DivvieViewState) {
        for (i in 0 until MAX_GUESTS) {
            val view = bowlsList.getChildAt(i)
            if (i < viewState.personList.size) {
                setVisibilityAttributes(i, viewState.personList, view)
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
            if (viewState.isClickableBowls) {
                if (viewState.tempItemListOfIndex.contains(i)) {
                    changeColor(view, Color.WHITE)
                } else {
                    changeColor(view,Color.DKGRAY)
                }
                view.setOnClickListener {
                    viewModel.onEvent(BowlsViewEvent.ClickBowl(i))
                }
            } else {
                changeColor(view, Color.LTGRAY)
                view.setOnClickListener {
                    val person: Serializable = viewModel.getPersonDetail(i)
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra(PERSON, person)
                    startActivity(intent)
                }
            }
        }
    }
}