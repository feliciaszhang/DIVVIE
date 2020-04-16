package com.example.divvie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders

class SplitFragment : Fragment() {
    companion object {
        fun newInstance() = SplitFragment()
    }
    private lateinit var viewModel: SharedViewModel
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
        viewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        val numberOfPeople = viewModel.getNumberOfPeople()
        val subtotal = viewModel.getSubtotal()
        val equalPrice: Double
        if (numberOfPeople != null && subtotal != null) {
            equalPrice = subtotal / numberOfPeople
        }


        equalButton.setOnClickListener {  }

        individualButton.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.info_fragment_layout, ItemFragment.newInstance())
                .commit()
        }

        backButton.setOnClickListener {  }
    }
}