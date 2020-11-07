package tk.zwander.rebooter.ui.qs

import android.os.Build
import androidx.annotation.RequiresApi
import tk.zwander.rebooter.util.customCommand

@RequiresApi(Build.VERSION_CODES.N)
class QuickRebootTile : RebooterTile() {
    override fun onClick() {
        customCommand("killall system_server")
    }
}