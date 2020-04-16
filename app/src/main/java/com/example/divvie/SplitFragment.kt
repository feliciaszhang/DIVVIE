package com.example.divvie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplitFragment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.split_fragment)

        val numberOfPeople = intent.getStringExtra(NUMBER_OF_PEOPLE)
        val subtotal = intent.getStringExtra(SUBTOTAL)
        val tax = intent.getStringExtra(TAX)

//        val textView = findViewById<TextView>(R.id.textView).apply { text = message }
    }
}