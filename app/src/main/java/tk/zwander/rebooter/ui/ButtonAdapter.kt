package tk.zwander.rebooter.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.power_button.view.*
import tk.zwander.rebooter.R
import tk.zwander.rebooter.data.*
import tk.zwander.rebooter.util.handleReboot

class ButtonAdapter : RecyclerView.Adapter<ButtonAdapter.ButtonHolder>() {
    private val items = ArrayList<ButtonData>()

    init {
        items.addAll(
            arrayListOf(
                ShutDownButtonData(
                    R.drawable.power,
                    R.string.shut_down,
                    R.color.shut_down_1,
                    R.color.shut_down_2
                ),
                RebootButtonData(
                    R.drawable.restart,
                    R.string.reboot,
                    R.color.reboot_1,
                    R.color.reboot_2
                ),
                CustomCommandButtonData(
                    R.drawable.cached,
                    R.string.quick_reboot,
                    R.color.quick_reboot_1,
                    R.color.quick_reboot_2,
                    "killall system_server"
                ),
                CustomCommandButtonData(
                    R.drawable.cancel,
                    R.string.kill_system_ui,
                    R.color.kill_sysui_1,
                    R.color.kill_sysui_2,
                    "killall com.android.systemui"
                ),
                SafeModeButtonData(
                    R.drawable.shield_check,
                    R.string.safe_mode,
                    R.color.safe_mode_1,
                    R.color.safe_mode_2
                ),
                RebootButtonData(
                    R.drawable.bug,
                    R.string.recovery,
                    R.color.recovery_1,
                    R.color.recovery_2,
                    "recovery"
                ),
                RebootButtonData(
                    R.drawable.hammer_wrench,
                    R.string.fastboot,
                    R.color.fastboot_1,
                    R.color.fastboot_2,
                    "bootloader"
                ),
                RebootButtonData(
                    R.drawable.hammer_wrench,
                    R.string.fastbootd,
                    R.color.fastbootd_1,
                    R.color.fastbootd_2,
                    "fastboot"
                ),
                RebootButtonData(
                    R.drawable.progress_download,
                    R.string.download,
                    R.color.download_1,
                    R.color.download_2,
                    "download"
                )
            )
        )
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

                val backgroundDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TL_BR, intArrayOf(
                        ContextCompat.getColor(context, data.startColor),
                        ContextCompat.getColor(context, data.endColor)
                    )
                )

                power_background.setImageDrawable(backgroundDrawable)
                power_frame.setCardBackgroundColor(Color.TRANSPARENT)

                power_frame.setOnClickListener {
                    val newData = items[adapterPosition]
                    newData.handleReboot()
                }
            }
        }
    }
}