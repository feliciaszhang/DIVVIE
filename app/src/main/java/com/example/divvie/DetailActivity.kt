package com.example.divvie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.TextView
import com.example.divvie.data.Person

class DetailActivity : AppCompatActivity() {
    private lateinit var name: TextView
    private lateinit var subtotal: TextView
    private lateinit var tax: TextView
    private lateinit var tip: TextView
    private lateinit var person: Person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.start_enter, R.anim.start_exit)
        setContentView(R.layout.detail_activity)
        name = findViewById(R.id.name)
        subtotal = findViewById(R.id.subtotal_amount)
        tax = findViewById(R.id.tax_amount)
        tip = findViewById(R.id.tip_amount)
        person = intent.getSerializableExtra(PERSON) as Person

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val w = dm.widthPixels
        val h = dm.heightPixels

        window.setLayout((w * 0.5).toInt(), (h * 0.3).toInt())

        val params = window.attributes
        params.gravity = Gravity.CENTER
        params.x = -(w * 0.5).toInt()
        params.y = 0

        window.attributes = params

        render()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.finish_enter, R.anim.finish_exit)
    }

    private fun render() {
        name.text = person.id.toString()
        subtotal.text = person.subtotal?.toString() ?: "null"
        tax.text = person.tax?.toString() ?: "null"
        tip.text = person.tip?.toString() ?: "null"
    }
}
