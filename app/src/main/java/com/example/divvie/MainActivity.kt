package com.example.divvie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.divvie.data.Item
import com.example.divvie.data.Person
import com.example.divvie.fragments.BowlsFragment
import com.example.divvie.fragments.InputFragment

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: DivvieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel = ViewModelProviders.of(this).get(DivvieViewModel::class.java)
        viewModel.setDisplayPrices(false)
        for (i in 0 until NUMBER_OF_PEOPLE_DEFAULT) {
            viewModel.insertPerson(Person(id = i))
        }
        viewModel.setSubtotal(AMOUNT_DEFAULT)
        viewModel.setTax(AMOUNT_DEFAULT)
        viewModel.setTip(AMOUNT_DEFAULT)
        viewModel.setSelectPerson(false)
        viewModel.setTempItem(Item())

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.info_fragment_layout, InputFragment.newInstance())
                .commit()
            supportFragmentManager.beginTransaction().replace(R.id.bowls_fragment_layout, BowlsFragment.newInstance())
                .commit()
        }
    }
}
