package tk.zwander.rebooter.ui.qs

import android.os.Build
import androidx.annotation.RequiresApi
import tk.zwander.rebooter.util.customCommand

@RequiresApi(Build.VERSION_CODES.N)
class KillSystemUITile : RebooterTile() {
    override fun onClick() {
        customCommand("killall com.android.systemui")
    }
}