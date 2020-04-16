package com.example.divvie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private var numberOfPeople = NUMBER_OF_PEOPLE_DEFAULT
    private var subtotal = AMOUNT_DEFAULT
    private var tax = AMOUNT_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val numberOfPeopleText: TextView = findViewById(R.id.number_of_people)
        val upButton: ImageButton = findViewById(R.id.up_button)
        val downButton: ImageButton = findViewById(R.id.down_button)
        val editSubtotalText: EditText = findViewById(R.id.editSubtotal)
        val editTaxText: EditText = findViewById(R.id.editTax)
        val calculateButton: Button = findViewById(R.id.calculate)
        val bowlsList: LinearLayout = findViewById(R.id.bowls)

        numberOfPeopleText.text = numberOfPeople.toString()
        editSubtotalText.hint = subtotal.toString()
        editTaxText.hint = tax.toString()
        calculateButton.isEnabled = false
        for (i in 0 until numberOfPeople) {
            bowlsList[i].visibility = View.VISIBLE
        }

        upButton.setOnClickListener {
            var num = numberOfPeopleText.text.toString().toInt()
            if (num < MAX_NUMBER_OF_PEOPLE) {
                bowlsList[num].visibility = View.VISIBLE
                num += 1
                numberOfPeopleText.text = num.toString()
                numberOfPeople = num
            }
        }

        downButton.setOnClickListener {
            var num = numberOfPeopleText.text.toString().toInt()
            if (num > MIN_NUMBER_OF_PEOPLE) {
                bowlsList[num - 1].visibility = View.GONE
                num -= 1
                numberOfPeopleText.text = num.toString()
                numberOfPeople = num
            }
        }

        editSubtotalText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (editSubtotalText.text.toString() != "") {
                    subtotal = editSubtotalText.text.toString().toDouble()
                    calculateButton.isEnabled = true
                }
            }
        }

        editTaxText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (editTaxText.text.toString() != "") {
                    tax = editTaxText.text.toString().toDouble()
                }
            }
        }

        calculateButton.setOnClickListener {
            editSubtotalText.isEnabled = false
            editTaxText.isEnabled = false
            Log.d("*****", numberOfPeople.toString())
            Log.d("*****", subtotal.toString())
            Log.d("*****", tax.toString())
        }
    }
}
