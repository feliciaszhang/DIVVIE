package com.example.divvie

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.divvie.fragments.BowlsFragment
import com.example.divvie.fragments.InputFragment
import android.graphics.Rect
import android.widget.EditText
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: DivvieViewModel

    // TODO personal breakdown calculation discrepency
    // TODO cannot be 0 warning
    // TODO tip cannot be over 100%
    // TODO percentage tip rounding

    // TODO fix icon and launcher image
    // TODO icons for navigation?
    // TODO more colors?

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel = ViewModelProviders.of(this).get(DivvieViewModel::class.java)
        viewModel.onEvent(DivvieViewEvent.DisplayActivity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.info_fragment_layout, InputFragment.newInstance()
            ).commit()
            supportFragmentManager.beginTransaction().replace(
                R.id.bowls_fragment_layout, BowlsFragment.newInstance()
            ).commit()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
