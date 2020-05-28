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

    // TODO format number to 2 decimal
    // TODO launcher background image
    // TODO flash final split bowls
    // TODO icons?
    // TODO more colors?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel = ViewModelProviders.of(this).get(DivvieViewModel::class.java)
        viewModel.onEvent(MainEvent.DisplayActivity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.info_fragment_layout, InputFragment.newInstance()
            ).commit()
            supportFragmentManager.beginTransaction().replace(
                R.id.bowls_fragment_layout, BowlsFragment.newInstance()
            ).commit()
        }
    }
}
