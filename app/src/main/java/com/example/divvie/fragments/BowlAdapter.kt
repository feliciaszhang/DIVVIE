package com.example.divvie.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import com.example.divvie.R
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.divvie.BowlsViewEvent
import com.example.divvie.DivvieViewModel
import com.example.divvie.data.Person

class BowlAdapter(
    context: Context,
    private val viewModel: DivvieViewModel,
    var clickable: Boolean?
) : BaseAdapter() {

    private lateinit var bowlRow: LinearLayout
    private lateinit var price: LinearLayout
    private lateinit var image: ImageView
    private lateinit var currency: TextView
    private lateinit var priceAmount: TextView
    private var listOfPerson: List<Person>? = viewModel.getAllPerson().value

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int { return listOfPerson?.size ?: 0 }

    override fun getItem(position: Int): Any {
        val list = listOfPerson
        return if (list != null) {
            list[position]
        } else {
            return Person(0)
        }
    }

    override fun getItemId(position: Int): Long { return position.toLong() }

    fun setData(newData: List<Person>) {
        listOfPerson = newData
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.bowl, parent, false)
        bowlRow = rowView.findViewById(R.id.bowlRow)
        price = rowView.findViewById(R.id.price)
        image = rowView.findViewById(R.id.imageView)
        currency = rowView.findViewById(R.id.currency)
        priceAmount = rowView.findViewById(R.id.price_amount)
        val person = listOfPerson!![position]
        image.setImageResource(R.drawable.bowl)
        displayPrices(person)
        if (clickable != null && clickable == true) {
            changeColor(bowlRow, Color.DKGRAY)
            bowlRow.isClickable = true
            bowlRow.setOnClickListener {
                viewModel.onEvent(BowlsViewEvent.ClickBowl(position))
            }
        } else {
            changeColor(bowlRow, Color.LTGRAY)
            bowlRow.isClickable = false
        }

        return rowView
    }

    private fun changeColor(view: View, color: Int) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val currency: TextView = view.findViewById(R.id.currency)
        val priceAmount: TextView = view.findViewById(R.id.price_amount)
        currency.setTextColor(color)
        priceAmount.setTextColor(color)
        image.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private fun displayPrices(person: Person) {
        val personalSubtotal = person.personalSubtotal
        val personalTax = person.personalTax ?: 0.0
        val personalTip = person.personalTip ?: 0.0
        val personalTempPrice = person.personalTempPrice ?: 0.0
        if (personalSubtotal != null) {
            price.visibility = View.VISIBLE
            val personalTotal = personalSubtotal + personalTax + personalTip + personalTempPrice
            priceAmount.text = personalTotal.toString()
        } else {
            price.visibility = View.GONE
        }
    }

//    private fun split(listOfIndex: ArrayList<Int>) {
//        for (i in 0 until viewModel.getNumberOfPeopleStatic()) {
//            val view = bowlRow.getChildAt(i)
//            if (listOfIndex.contains(i)) {
//                changeColor(view, Color.WHITE)
//            } else {
//                changeColor(view,Color.DKGRAY)
//            }
//            viewModel.split(i)
//        }
//    }
}