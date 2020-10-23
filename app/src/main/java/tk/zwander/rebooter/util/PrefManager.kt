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

    val defaultButtons = arrayListOf(
        ShutDownButtonData(
            "power",
            "shut_down",
            "shut_down_1",
            "shut_down_2"
        ),
        RebootButtonData(
            "restart",
            "reboot",
            "reboot_1",
            "reboot_2"
        ),
        CustomCommandButtonData(
            "cached",
            "quick_reboot",
            "quick_reboot_1",
            "quick_reboot_2",
            "killall system_server"
        ),
        CustomCommandButtonData(
            "cancel",
            "kill_system_ui",
            "kill_sysui_1",
            "kill_sysui_2",
            "killall com.android.systemui"
        ),
        SafeModeButtonData(
            "shield_check",
            "safe_mode",
            "safe_mode_1",
            "safe_mode_2"
        ),
        RebootButtonData(
            "bug",
            "recovery",
            "recovery_1",
            "recovery_2",
            "recovery"
        ),
        RebootButtonData(
            "hammer_wrench",
            "fastboot",
            "fastboot_1",
            "fastboot_2",
            "bootloader"
        ),
        RebootButtonData(
            "hammer_wrench",
            "fastbootd",
            "fastbootd_1",
            "fastbootd_2",
            "fastboot"
        ),
        RebootButtonData(
            "progress_download",
            "download",
            "download_1",
            "download_2",
            "download"
        )
    )

    private val gson = GsonBuilder()
        .registerTypeHierarchyAdapter(ButtonData::class.java, GenericSerializer())
        .create()

    fun getPowerButtons(): ArrayList<ButtonData> {
        val buttonsString = sharedPreferences.getString(KEY_BUTTONS, null)

        if (buttonsString.isNullOrBlank()) {
            return defaultButtons
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