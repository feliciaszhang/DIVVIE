package com.example.divvie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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
