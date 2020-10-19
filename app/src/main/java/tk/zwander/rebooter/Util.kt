package tk.zwander.rebooter

import com.topjohnwu.superuser.Shell

fun ButtonData.handleReboot() {
    when (this) {
        is RebootButtonData -> reboot(reason)
        is ShutDownButtonData -> shutDown()
        is SafeModeButtonData -> safeMode()
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
}