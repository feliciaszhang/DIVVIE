package com.felili.divvie

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.felili.divvie.fragments.BowlsFragment
import com.felili.divvie.fragments.InputFragment
import android.graphics.Rect
import android.widget.EditText
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.tubitv.fragmentoperator.activity.FoActivity
import com.tubitv.fragments.FragmentOperator

class MainActivity : FoActivity() {
    private lateinit var viewModel: DivvieViewModel

    // TODO store items / item name / edit item
    // TODO save user names so don't have to input each time
    // TODO save meal

    // TODO spacing in Item and Result
    // TODO backbutton navigation kinda wierd sometimes
    // TODO slider for percentage?
    // TODO translate

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel = ViewModelProviders.of(this).get(DivvieViewModel::class.java)
        viewModel.onEvent(DivvieViewEvent.DisplayActivity)
        FragmentOperator.setCurrentActivity(this)
        if (savedInstanceState == null) {
            FragmentOperator.showFragment(InputFragment())
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

    override fun getFragmentContainerResId(): Int {
        return R.id.info_fragment_layout
    }
}
