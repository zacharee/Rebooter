package tk.zwander.rebooter.util

import android.content.Context
import com.topjohnwu.superuser.Shell
import tk.zwander.rebooter.data.*

fun ButtonData.handleReboot() {
    when (this) {
        is RebootButtonData -> reboot(reason)
        is ShutDownButtonData -> shutDown()
        is SafeModeButtonData -> safeMode()
        is CustomCommandButtonData -> customCommand(command)
    }
}

fun reboot(reason: String?) {
    Shell.su("svc power reboot $reason")
        .exec()
}

fun safeMode() {
    Shell.su(
        "setprop persist.sys.safemode",
        "svc power reboot"
    ).exec()
}

fun shutDown() {
    Shell.su("svc power shutdown")
        .exec()
}

fun customCommand(command: String) {
    Shell.su(command).exec()
}

val Context.isTouchWiz: Boolean
    get() = packageManager.hasSystemFeature("com.samsung.feature.samsung_experience_mobile")
