package tk.zwander.rebooter.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import kotlin.math.floor


/**
 * A version of RecyclerView that uses a GridLayoutManager and automatically
 * sets the number of columns based on a given column width and the width
 * of the RecyclerView.
 */
class AutoFitGridRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    private val manager = GridLayoutManager(context, 1)

    private var columnWidth = -1

    init {
        val attrsArray = intArrayOf(
            android.R.attr.columnWidth
        )
        val array: TypedArray = context.obtainStyledAttributes(
            attrs, attrsArray
        )
        columnWidth = array.getDimensionPixelSize(0, -1)
        array.recycle()

        layoutManager = manager
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)

        //This "post" prevents a weird measurement loop
        //when in a dialog.
        post {
            if (columnWidth > 0) {
                val spanCount = 1
                    .coerceAtLeast(floor(measuredWidth.toFloat() / columnWidth).toInt())
                    .run {
                        val itemCount = manager.itemCount

                        if (itemCount > 0) {
                            coerceAtMost(itemCount)
                        } else {
                            this
                        }
                    }
                    .run {
                        val itemCount = manager.itemCount

                        val count = this

                        if (itemCount > 0 && itemCount % count != 0) {
                            for (i in count - 1 downTo 2) {
                                if (itemCount % i == 0) {
                                    return@run i
                                }
                            }

                            count
                        } else {
                            count
                        }
                    }

                itemAnimator?.isRunning {
                    if (spanCount != manager.spanCount) {
                        TransitionManager.beginDelayedTransition(this)
                        manager.spanCount = spanCount
                    }
                }
            }
        }
    }
}