package com.example.divvie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.divvie.R
import com.example.divvie.DivvieViewModel
import com.example.divvie.DivvieViewState
import com.example.divvie.SplitViewEvent

class SplitFragment : Fragment() {
    companion object {
        fun newInstance() = SplitFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var equalButton: Button
    private lateinit var individualButton: Button
    private lateinit var calculateButton: Button
    private lateinit var backToInputButton: Button
    private lateinit var backToEqualButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.split_fragment, container, false)
        equalButton = fragment.findViewById(R.id.split_equally_button)
        individualButton = fragment.findViewById(R.id.split_individually_button)
        calculateButton = fragment.findViewById(R.id.calculate)
        backToInputButton = fragment.findViewById(R.id.back_to_input_button)
        backToEqualButton = fragment.findViewById(R.id.back_to_equal_button)
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(SplitViewEvent.DisplayFragment)
        viewModel.viewStateObservable.observe(viewLifecycleOwner, Observer { render(it) })

        equalButton.setOnClickListener {
            viewModel.onEvent(SplitViewEvent.SplitEqually)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, ResultFragment.newInstance()
            ).commit()
        }

        individualButton.setOnClickListener {
            viewModel.onEvent(SplitViewEvent.EnterIndividually)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, ItemFragment.newInstance()
            ).commit()
        }

        calculateButton.setOnClickListener {
            viewModel.onEvent(SplitViewEvent.Calculate)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout,
                ResultFragment.newInstance()
            ).commit()
        }

        backToInputButton.setOnClickListener {
            viewModel.onEvent(SplitViewEvent.BackToInput)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, InputFragment.newInstance()
            ).commit()
        }

        backToEqualButton.setOnClickListener {
            viewModel.onEvent(SplitViewEvent.BackToEqual)
        }
    }

    private fun render(viewState: DivvieViewState) {
        if (viewState.leftover == 0.0) {
            calculateButton.visibility = View.VISIBLE
            backToEqualButton.visibility = View.VISIBLE
            backToInputButton.visibility = View.GONE
            equalButton.visibility = View.GONE
            individualButton.visibility = View.GONE
        } else {
            calculateButton.visibility = View.GONE
            backToEqualButton.visibility = View.GONE
            backToInputButton.visibility = View.VISIBLE
            equalButton.visibility = View.VISIBLE
            individualButton.visibility = View.VISIBLE
        }
    }
}