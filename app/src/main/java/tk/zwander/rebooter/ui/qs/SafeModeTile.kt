package tk.zwander.rebooter.ui.qs

import android.os.Build
import androidx.annotation.RequiresApi
import tk.zwander.rebooter.util.safeMode

@RequiresApi(Build.VERSION_CODES.N)
class SafeModeTile : RebooterTile() {
    override fun onClick() {
        safeMode()
    }
}