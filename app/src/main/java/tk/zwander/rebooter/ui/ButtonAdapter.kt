package tk.zwander.rebooter.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.rainbow.Rainbow
import com.skydoves.rainbow.RainbowOrientation
import com.skydoves.rainbow.contextColor
import kotlinx.android.synthetic.main.power_button.view.*
import tk.zwander.rebooter.R
import tk.zwander.rebooter.data.ButtonData
import tk.zwander.rebooter.util.handleReboot
import java.util.*
import kotlin.collections.ArrayList


/**
 * The adapter for the power buttons.
 */
class ButtonAdapter(private val removalCallback: (ButtonAdapter, ButtonData) -> Unit) : RecyclerView.Adapter<ButtonAdapter.ButtonHolder>() {
    val items = ArrayList<ButtonData>()

    var selectedIndex = -1
        set(value) {
            field = value

            indexObservable.notifyObservers(field)
        }

    private val indexObservable = object : Observable() {
        override fun notifyObservers(arg: Any?) {
            setChanged()
            super.notifyObservers(arg)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonHolder {
        return ButtonHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.power_button, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ButtonHolder, position: Int) {
        holder.onBind(items[position])
    }

    fun setItems(items: List<ButtonData>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: ButtonData) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    /**
     * The ViewHolder class for a given button.
     */
    @SuppressLint("ClickableViewAccessibility")
    inner class ButtonHolder(view: View) : RecyclerView.ViewHolder(view) {
        var isDragging = false

        init {
            itemView.apply {
                indexObservable.addObserver { _, _ ->
                    remove_button.isVisible = selectedIndex == adapterPosition
                }

                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
                        power_frame.handleEvent(event)
                    }
                    false
                }

                //Respond to click events.
                power_frame.setOnClickListener {
                    //It's possible the item this ViewHolder corresponds
                    //to has changed, so grab it based on the current
                    //position.
                    if (!isDragging) {
                        val pos = adapterPosition
                        if (selectedIndex == pos) {
                            selectedIndex = -1
                        } else {
                            val newData = items[pos]
                            newData.handleReboot()
                        }
                    }
                }

                remove_button.setOnClickListener {
                    val pos = adapterPosition
                    val data = items.removeAt(pos)

                    notifyItemRemoved(pos)
                    selectedIndex = -1

                    removalCallback(this@ButtonAdapter, data)
                }

                val drawable = Rainbow(remove_button).palette {
                    +contextColor(R.color.remove_1)
                    +contextColor(R.color.remove_2)
                }.withAlpha(255)
                    .getDrawable(RainbowOrientation.DIAGONAL_TOP_LEFT)

                remove_background.setImageDrawable(drawable)
            }
        }

        fun onBind(data: ButtonData) {
            itemView.apply {
                //Set the proper icon and label.
                power_icon.setImageDrawable(data.loadIcon(context))
                power_text.text = data.loadName(context)

                remove_button.isVisible = selectedIndex == adapterPosition

                //Construct a gradient for the button background,
                //based off the colors specified in the ButtonData.
                val backgroundDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TL_BR, intArrayOf(
                        data.loadStartColor(context),
                        data.loadEndColor(context)
                    )
                )

                //Set up the background.
                power_background.setImageDrawable(backgroundDrawable)
                power_frame.setCardBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}