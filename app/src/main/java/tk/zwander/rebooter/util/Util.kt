package tk.zwander.rebooter.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.topjohnwu.superuser.Shell
import tk.zwander.rebooter.data.*

/**
 * Handle a given ButtonData instance's action.
 */
fun ButtonData.handleReboot() {
    when (this) {
        is RebootButtonData -> reboot(reason)
        is ShutDownButtonData -> shutDown()
        is SafeModeButtonData -> safeMode()
        is CustomCommandButtonData -> customCommand(command)
    }
}

/**
 * Tell the system to reboot with the specified
 * reason.
 *
 * If the reason is null, this will technically
 * pass `svc power reboot null`, but Android ignores
 * unknown reasons, so it doesn't cause issues.
 */
fun reboot(reason: String?) {
    Shell.su("svc power reboot $reason")
        .exec()
}

/**
 * Tell the system to reboot into safe mode.
 */
fun safeMode() {
    Shell.su(
        "setprop persist.sys.safemode",
        "svc power reboot"
    ).exec()
}

/**
 * Tell the system to shut down.
 */
fun shutDown() {
    Shell.su("svc power shutdown")
        .exec()
}

/**
 * Execute the given command.
 */
fun customCommand(command: String) {
    Shell.su(command).exec()
}

/**
 * Check if the current device is running
 * Samsung's Android skin (TouchWiz, Grace UX,
 * Samsung Experience, One UI)
 */
val Context.isTouchWiz: Boolean
    get() = packageManager.hasSystemFeature("com.samsung.feature.samsung_experience_mobile")

/**
 * Get the PrefManager instance.
 */
val Context.prefManager: PrefManager
    get() = PrefManager.getInstance(this)

/**
 * A convenience method for Gson to automatically
 * provide a class' type.
 */
fun <T> Gson.fromJson(json: String): T {
    return fromJson(json, object : TypeToken<T>() {}.type)
}
