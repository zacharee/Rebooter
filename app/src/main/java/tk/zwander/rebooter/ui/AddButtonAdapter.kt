package tk.zwander.rebooter.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import kotlinx.android.synthetic.main.power_button.view.*
import tk.zwander.rebooter.R
import tk.zwander.rebooter.data.ButtonData

/**
 * The Adapter for buttons that are available to be added to the main screen.
 */
class AddButtonAdapter(
    private val context: Context,
    private val selectionCallback: (ButtonData) -> Unit
) : RecyclerView.Adapter<AddButtonAdapter.AddButtonVH>() {
    /**
     * The available buttons, sorted alphabetically.
     */
    private val items =
        SortedList(ButtonData::class.java, object : SortedListAdapterCallback<ButtonData>(this) {
            override fun areContentsTheSame(oldItem: ButtonData?, newItem: ButtonData?): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(item1: ButtonData?, item2: ButtonData?): Boolean {
                return item1 == item2
            }

            override fun compare(o1: ButtonData, o2: ButtonData): Int {
                return o1.loadName(context).compareTo(o2.loadName(context))
            }
        })

    override fun getItemCount(): Int {
        return items.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddButtonVH {
        return AddButtonVH(
            LayoutInflater.from(context)
                .inflate(R.layout.power_button, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AddButtonVH, position: Int) {
        holder.onBind(items.get(position))
    }

    /**
     * Set the currently available buttons.
     */
    fun setItems(items: List<ButtonData>) {
        this.items.replaceAll(items)
    }

    /**
     * The ViewHolder for an available button.
     * This is very similar to the ViewHolder for
     * active buttons, but has different click handling.
     */
    inner class AddButtonVH(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.power_frame.setOnClickListener {
                //This button was tapped. Invoke the callback
                //so it gets added to the main list.
                val pos = adapterPosition
                selectionCallback(items.get(pos))
            }
        }

        /**
         * Set the proper data for this button.
         */
        fun onBind(data: ButtonData) {
            itemView.apply {
                //Set the proper icon and label.
                power_icon.setImageDrawable(data.loadIcon(context))
                power_text.text = data.loadName(context)

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