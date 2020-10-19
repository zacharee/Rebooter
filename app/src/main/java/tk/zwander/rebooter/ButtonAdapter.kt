package tk.zwander.rebooter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.power_button.view.*

class ButtonAdapter : RecyclerView.Adapter<ButtonAdapter.ButtonHolder>() {
    private val items = ArrayList<ButtonData>()

    init {
        items.addAll(arrayListOf(
            ShutDownButtonData(R.drawable.power, R.string.shut_down, R.color.shut_down),
            RebootButtonData(R.drawable.restart, R.string.reboot, R.color.reboot),
            SafeModeButtonData(R.drawable.shield_check, R.string.safe_mode, R.color.safe_mode),
            RebootButtonData(R.drawable.bug, R.string.recovery, R.color.recovery, "recovery"),
            RebootButtonData(R.drawable.hammer_wrench, R.string.fastboot, R.color.fastboot, "bootloader"),
            RebootButtonData(R.drawable.hammer_wrench, R.string.fastbootd, R.color.fastbootd, "fastboot"),
            RebootButtonData(R.drawable.progress_download, R.string.download, R.color.download, "download")
        ))
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

    inner class ButtonHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun onBind(data: ButtonData) {
            itemView.apply {
                power_icon.setImageResource(data.icon)
                power_text.setText(data.name)
                power_frame.setCardBackgroundColor(
                    ContextCompat.getColor(context, data.color))

                power_frame.setOnClickListener {
                    val newData = items[adapterPosition]
                    newData.handleReboot()
                }
            }
        }
    }
}