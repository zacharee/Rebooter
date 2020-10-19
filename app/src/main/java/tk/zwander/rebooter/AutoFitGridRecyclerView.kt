package tk.zwander.rebooter

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.floor


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
        if (columnWidth > 0) {
            val spanCount = 1.coerceAtLeast(floor(measuredWidth.toFloat() / columnWidth).toInt())
            manager.spanCount = spanCount
        }
    }
}