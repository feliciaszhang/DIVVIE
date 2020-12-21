package com.felili.divvie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProviders
import com.felili.divvie.DivvieViewEvent
import com.felili.divvie.R
import com.felili.divvie.DivvieViewModel
import com.tubitv.fragmentoperator.fragment.FoFragment
import com.tubitv.fragmentoperator.fragment.annotation.SingleInstanceFragment
import com.tubitv.fragments.FragmentOperator

@SingleInstanceFragment
class SplitFragment : FoFragment() {
    private lateinit var viewModel: DivvieViewModel
    private lateinit var resultButton: Button
    private lateinit var individualButton: Button
    private lateinit var backButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.split_fragment, container, false)
        resultButton = fragment.findViewById(R.id.get_result)
        individualButton = fragment.findViewById(R.id.split_individually_button)
        backButton = fragment.findViewById(R.id.back_button)
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DivvieViewModel::class.java)
        viewModel.onEvent(DivvieViewEvent.DisplaySplitFragment)

        resultButton.setOnClickListener {
            FragmentOperator.showFragment(ResultFragment(), clearStack = false, skipOnPop = true)
        }

        individualButton.setOnClickListener {
            FragmentOperator.showFragment(ItemFragment(), clearStack = false, skipOnPop = true)
        }

        backButton.setOnClickListener {
            FragmentOperator.showFragment(InputFragment(), clearStack = true, skipOnPop = true)
        }
    }
}