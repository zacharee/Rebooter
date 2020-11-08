package tk.zwander.rebooter.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import com.google.android.material.card.MaterialCardView

class TouchResponsiveMaterialCardView(context: Context, attrs: AttributeSet) : MaterialCardView(context, attrs) {
//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        handleEvent(ev)
//        return super.onInterceptTouchEvent(ev)
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        handleEvent(event)
//        return super.onTouchEvent(event)
//    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN || ev?.action == MotionEvent.ACTION_UP) {
            handleEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun handleEvent(ev: MotionEvent?) {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                animate().cancel()
                animate()
                    .scaleX(0.90f)
                    .scaleY(0.90f)
                    .setDuration(400)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }

            MotionEvent.ACTION_UP -> {
                animate().cancel()
                animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(400)
                    .setInterpolator(AnticipateInterpolator())
                    .start()
            }
        }
    }
}