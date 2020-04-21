package com.example.divvie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.divvie.database.Item
import com.example.divvie.database.Person
import com.example.divvie.fragments.BowlsFragment
import com.example.divvie.fragments.InputFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.info_fragment_layout, InputFragment.newInstance())
                .commit()
            supportFragmentManager.beginTransaction().replace(R.id.bowls_fragment_layout, BowlsFragment.newInstance())
                .commit()
        }
    }
}
