package com.felili.divvie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.felili.divvie.*

class CalculateFragment : Fragment() {
    companion object {
        fun newInstance() = CalculateFragment()
    }
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
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout,
                ResultFragment.newInstance()
            ).commit()
        }

        backButton.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(
                R.id.info_fragment_layout, ItemFragment.newInstance()
            ).commit()
        }
    }
}