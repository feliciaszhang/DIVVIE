package com.felili.divvie

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class PopupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_activity)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val w = dm.widthPixels
        val h = dm.heightPixels * 0.27

        window.setLayout(w, h.toInt())

        val params = window.attributes
        params.gravity = Gravity.CENTER
        params.x = 0
        params.y = (dm.heightPixels - h / 2).toInt()

        window.attributes = params

        val reportButton = findViewById<Button>(R.id.report)
        val rateButton = findViewById<Button>(R.id.rate)

        reportButton.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "message/rfc822"
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf("felili.sitao@gmail.com"))
            i.putExtra(Intent.EXTRA_SUBJECT, "[divvie] Suggestions / Issues")
            try {
                startActivity(Intent.createChooser(i, "Send mail..."))
                finish()
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    "There are no email clients installed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        rateButton.setOnClickListener {
            val uri: Uri = Uri.parse("market://details?id=" + this.packageName)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)
                    )
                )
            }
        }
    }
}