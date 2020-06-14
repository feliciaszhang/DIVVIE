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

    fun clean(numString: String): String {
        var dotPos = -1
        val len = numString.length - 1
        for (i in numString.indices) {
            if (numString[i] == '.') {
                dotPos = i
            }
        }
        if (dotPos >= 0) {
            return when (len - dotPos) {
                0 -> numString + "00"
                1 -> numString + "0"
                2 -> numString
                else -> throw Exception("---------------- more than 2 decimals -------------------")
            }
        }
        return numString + ".00"
    }
}