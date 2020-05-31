package com.example.divvie

import android.text.Spanned
import android.text.InputFilter


class CurrencyInputFilter(private val decimalDigits: Int = 2) : InputFilter {

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        var dotPos = -1
        val len = dest.length
        for (i in 0 until len) {
            if (dest[i] == '.') {
                dotPos = i
                break
            }
        }
        if (dotPos >= 0) {
            if (source == ".") { return "" }
            if (dend <= dotPos) { return null }
            if (len - dotPos > decimalDigits) { return "" }
        } else if (source == "." && (len - dend) > decimalDigits) { return "" }
        return null
    }

    fun convert(numString: String): String {
        var dotPos = -1
        val len = numString.length - 1
        for (i in numString.indices) {
            if (numString[i] == '.') {
                dotPos = i
            }
        }
        if (dotPos >= 0) {
            when (len - dotPos) {
                2 -> return numString
                1 -> return numString + "0"
                0 -> return numString + "00"
            }
        }
        return numString + ".00"
    }
}