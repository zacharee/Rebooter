package tk.zwander.rebooter

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        init {
            Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            setShowWhenLocked(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }

        buttons.adapter = ButtonAdapter()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        finishWithAnimation()
    }

    override fun onPause() {
        super.onPause()

        finishWithAnimation()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finishWithAnimation()
    }

    private fun finishWithAnimation() {
        finish()
        overridePendingTransition(0, R.anim.exit_anim)
    }
}