package com.example.divvie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.divvie.DivvieViewEvent
import com.example.divvie.R
import com.example.divvie.DivvieViewModel

class SplitFragment : Fragment() {
    companion object {
        fun newInstance() = SplitFragment()
    }
    private lateinit var viewModel: DivvieViewModel
    private lateinit var equalButton: Button
    private lateinit var individualButton: Button
    private lateinit var backButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.split_fragment, container, false)
        equalButton = fragment.findViewById(R.id.split_equally_button)
        individualButton = fragment.findViewById(R.id.split_individually_button)
        backButton = fragment.findViewById(R.id.back_button)
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(DivvieViewEvent.DisplaySplitFragment)

        equalButton.setOnClickListener {
            viewModel.onEvent(DivvieViewEvent.SplitToResult)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, ResultFragment.newInstance()
            ).commit()
        }

        individualButton.setOnClickListener {
            viewModel.onEvent(DivvieViewEvent.SplitToItem)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, ItemFragment.newInstance()
            ).commit()
        }

        backButton.setOnClickListener {
            viewModel.onEvent(DivvieViewEvent.SplitToInput)
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, InputFragment.newInstance()
            ).commit()
        }
    }
}