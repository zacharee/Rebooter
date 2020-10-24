package tk.zwander.rebooter.util

import android.view.animation.Interpolator
import androidx.recyclerview.widget.RecyclerView
import jp.wasabeef.recyclerview.animators.BaseItemAnimator

open class FadeScaleAnimator : BaseItemAnimator {
    constructor()
    constructor(interpolator: Interpolator) {
        this.interpolator = interpolator
    }

    override fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        holder.itemView.animate().apply {
            scaleX(0.95f)
            scaleY(0.95f)
            alpha(0f)
            duration = removeDuration
            interpolator = this@FadeScaleAnimator.interpolator
            setListener(DefaultRemoveAnimatorListener(holder))
            startDelay = getRemoveDelay(holder)
        }.start()
    }

    override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder) {
        holder.itemView.apply {
            scaleX = 0.95f
            scaleY = 0.95f
            alpha = 0f
        }
    }

    override fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        holder.itemView.animate().apply {
            scaleX(1f)
            scaleY(1f)
            alpha(1f)
            duration = addDuration
            interpolator = this@FadeScaleAnimator.interpolator
            setListener(DefaultAddAnimatorListener(holder))
            startDelay = getAddDelay(holder)
        }.start()
    }
}