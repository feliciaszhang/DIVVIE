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
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.divvie.BowlsViewEvent
import com.example.divvie.R
import com.example.divvie.DivvieViewModel
import com.example.divvie.data.Person

class BowlsFragment : Fragment() {
    companion object {
        fun newInstance() = BowlsFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var bowlList: ListView
    private lateinit var adapter: BowlAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        val fragment = inflater.inflate(R.layout.bowls_fragment, container, false)
        adapter = BowlAdapter(activity!!.applicationContext, viewModel, viewModel.getSelectPerson())
        bowlList = fragment.findViewById(R.id.bowlList)
        bowlList.adapter = adapter

        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.onEvent(BowlsViewEvent.DisplayFragment)
        viewModel.getAllPerson().observe(viewLifecycleOwner, Observer { updatePrices(it) })
        viewModel.selectPersonObservable.observe(viewLifecycleOwner, Observer { clickableBowls(it) })
    }

    private fun updatePrices(list: List<Person>) {
        adapter.setData(list)
    }

    private fun clickableBowls(bool: Boolean) {
        adapter.clickable = bool
    }
}