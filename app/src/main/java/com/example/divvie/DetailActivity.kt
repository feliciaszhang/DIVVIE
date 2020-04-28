package com.example.divvie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val w = dm.widthPixels
        val h = dm.heightPixels

        window.setLayout((w * 0.5).toInt(), (h * 0.5).toInt())

        val params = window.attributes
        params.gravity = Gravity.CENTER
        params.x = 0
        params.y = -20

        window.attributes = params
    }
}
