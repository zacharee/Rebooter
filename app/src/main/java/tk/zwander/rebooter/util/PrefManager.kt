package tk.zwander.rebooter.util

import android.content.Context
import android.content.ContextWrapper
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import tk.zwander.rebooter.R
import tk.zwander.rebooter.data.*

class PrefManager private constructor(context: Context) : ContextWrapper(context) {
    companion object {
        const val KEY_BUTTONS = "buttons_list"

        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: PrefManager(context.applicationContext).apply {
                instance = this
            }
        }
    }

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

    private val gson = GsonBuilder()
        .registerTypeHierarchyAdapter(ButtonData::class.java, GenericSerializer())
        .create()

    fun getPowerButtons(): ArrayList<ButtonData> {
        val default = arrayListOf(
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

        val buttonsString = sharedPreferences.getString(KEY_BUTTONS, null)

        if (buttonsString.isNullOrBlank()) {
            return default
        }

        val buttonsSet = gson.fromJson<ArrayList<String>>(buttonsString)
        val ret = ArrayList<ButtonData>()

        ret.addAll(buttonsSet.map { gson.fromJson(it, ButtonData::class.java) })

        return ret
    }

    fun setPowerButtons(buttons: List<ButtonData>) {
        sharedPreferences.edit {
            val set = ArrayList<String>()

            set.addAll(buttons.map { gson.toJson(it) })

            putString(KEY_BUTTONS, gson.toJson(set))
        }
    }
}