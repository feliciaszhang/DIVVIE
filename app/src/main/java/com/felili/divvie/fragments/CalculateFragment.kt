package com.felili.divvie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.felili.divvie.*
import com.tubitv.fragmentoperator.fragment.FoFragment
import com.tubitv.fragmentoperator.fragment.annotation.SingleInstanceFragment
import com.tubitv.fragments.FragmentOperator

@SingleInstanceFragment
class CalculateFragment : FoFragment() {
    private lateinit var resultButton: Button
    private lateinit var backButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragment = inflater.inflate(R.layout.calculate_fragment, container, false)
        resultButton = fragment.findViewById(R.id.result_button)
        backButton = fragment.findViewById(R.id.back_button)
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        resultButton.setOnClickListener {
            FragmentOperator.showFragment(ResultFragment(), clearStack = false, skipOnPop = true)
        }

        backButton.setOnClickListener {
            FragmentOperator.showFragment(ItemFragment(), clearStack = false, skipOnPop = false)
        }
    }
}