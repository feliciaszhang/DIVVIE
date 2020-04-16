package com.example.divvie

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SplitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split)

        val numberOfPeople = intent.getStringExtra(NUMBER_OF_PEOPLE)
        val subtotal = intent.getStringExtra(SUBTOTAL)
        val tax = intent.getStringExtra(TAX)

//        val textView = findViewById<TextView>(R.id.textView).apply { text = message }
    }
}