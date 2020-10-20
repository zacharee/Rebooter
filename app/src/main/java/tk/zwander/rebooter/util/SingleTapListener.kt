package tk.zwander.rebooter.util

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.abs

/**
 * An OnTouchListener implementation that detects single taps.
 *
 * This differs from an OnClickListener in that it doesn't allow swiping.
 * An OnClickListener will be triggered even if the user moved their finger,
 * as long as they remained touching the View.
 */
class SingleTapListener(private val callback: () -> Unit) : View.OnTouchListener {
    private var downX = 0f
    private var downY = 0f

    private var slop = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (slop == 0) {
            //Assign the framework touch slop value if needed.
            slop = ViewConfiguration.get(v.context).scaledTouchSlop
        }

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //Record the down coords.
                downX = event.x
                downY = event.y
                false
            }
            MotionEvent.ACTION_UP -> {
                //When the user lifts their finger,
                //check to make sure that they didn't
                //move beyond the touch slop.
                if (abs(downX - event.x) < slop
                    && abs(downY - event.y) < slop) {
                    //The user moved little enough to count as
                    //a tap, so trigger the callback.
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