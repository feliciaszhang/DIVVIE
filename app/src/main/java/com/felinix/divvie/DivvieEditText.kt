package com.felinix.divvie

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Shader
import android.graphics.LinearGradient
import android.util.AttributeSet
import android.widget.EditText


class DivvieEditText(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
    : EditText(context, attributeSet, defStyleAttr, defStyleRes) {

    private var color = 0xFF000000.toInt()
    private var horizontal: Int = 0 // how much the text is going outside

    constructor(context: Context) : this(context, null, R.attr.editTextStyle, R.style.EditTextStyle)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, R.attr.editTextStyle, R.style.EditTextStyle)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, R.style.EditTextStyle)

    override fun onScrollChanged(horiz: Int, vert: Int, oldHoriz: Int, oldVert: Int) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert)
        horizontal = horiz
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        color = currentTextColor
        val rightWidth = measuredWidth + horizontal
        val leftWidth = horizontal

        val rightPercent = measuredWidth * 40 / 100
        val leftPercent = if (horizontal < rightPercent) { horizontal } else { rightPercent }
        // gradually increase left gradient until a set length

        val widthPreLeft = horizontal
        val stopPreLeft = widthPreLeft.toFloat() / rightWidth.toFloat()

        val stopLeft = if (widthPreLeft > 0) {(leftWidth + leftPercent).toFloat() / rightWidth.toFloat()} else 0f
        val stopRight = if (layout.getLineWidth(0) > rightWidth) {
            (rightWidth - rightPercent).toFloat() / rightWidth.toFloat()
        } else {
            rightWidth.toFloat() / rightWidth.toFloat()
        }
        val gradient = LinearGradient(0f, 0f, rightWidth.toFloat(), 0f,
            intArrayOf(color, Color.TRANSPARENT, color, color, Color.TRANSPARENT),
            floatArrayOf(0f, stopPreLeft, stopLeft, stopRight, 1.0f),
            Shader.TileMode.CLAMP
        )
        if (text != null && text.length > 1 && layout.getLineWidth(0) > measuredWidth) {
            paint.shader = gradient
        } else {
            paint.shader = null
        }
        super.onDraw(canvas)
    }

}
