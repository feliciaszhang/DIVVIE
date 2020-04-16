package com.example.divvie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class BowlsFragment : Fragment() {
    companion object {
        fun newInstance() = BowlsFragment()
    }

    private lateinit var viewModel: SharedViewModel
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
        viewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        viewModel.numberOfPeopleObservable.observe(viewLifecycleOwner, Observer { displayBowls(it) })
    }

    private fun displayBowls(num: Int) {
        for (i in 0 until MAX_NUMBER_OF_PEOPLE) {
            if (i < num) {
                bowlsList.getChildAt(i).visibility = View.VISIBLE
            }
            else {
                bowlsList.getChildAt(i).visibility = View.GONE
            }
        }
    }
}