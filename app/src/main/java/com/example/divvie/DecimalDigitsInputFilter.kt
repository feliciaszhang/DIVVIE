package com.example.divvie

import android.text.Spanned
import android.text.InputFilter


class DecimalDigitsInputFilter(private val decimalDigits: Int) : InputFilter {

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        var dotPos = -1
        val len = dest.length
        for (i in 0 until len) {
            val char = dest[i]
            if (char == '.' || char == ',') {
                dotPos = i
                break
            }
        }
        if (dotPos >= 0) {
            if (source == "." || source == ",") { return "" }
            if (dend <= dotPos) { return null }
            if (len - dotPos > decimalDigits) { return "" }
        } else if (source == "." && (len - dend) > decimalDigits) { return "" }
        return null
    }
}