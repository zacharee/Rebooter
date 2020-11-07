package tk.zwander.rebooter.ui.qs

import android.os.Build
import androidx.annotation.RequiresApi
import tk.zwander.rebooter.util.shutDown

@RequiresApi(Build.VERSION_CODES.N)
class ShutDownTile : RebooterTile() {
    override fun onClick() {
        shutDown()
    }
}