package tk.zwander.rebooter.ui.qs

import android.os.Build
import androidx.annotation.RequiresApi
import tk.zwander.rebooter.util.reboot

@RequiresApi(Build.VERSION_CODES.N)
class RebootTile : RebooterTile() {
    override fun onClick() {
        reboot(null)
    }
}