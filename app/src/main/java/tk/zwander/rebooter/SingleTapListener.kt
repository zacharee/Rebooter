package tk.zwander.rebooter

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

class SingleTapListener(private val callback: () -> Unit) : View.OnTouchListener {
    private var downX = 0f
    private var downY = 0f

    private var slop = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (slop == 0) {
            slop = ViewConfiguration.get(v.context).scaledTouchSlop
        }

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                false
            }
            MotionEvent.ACTION_UP -> {
                if (abs(downX - event.x) < slop
                    && abs(downY - event.y) < slop) {
                    callback()
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }
}