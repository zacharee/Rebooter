package tk.zwander.rebooter

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    companion object {
        init {
            Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_REDIRECT_STDERR)
                    .setTimeout(10)
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
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

        val slop = ViewConfiguration.get(this).scaledTouchSlop

        var downX = 0f
        var downY = 0f

        buttons.adapter = ButtonAdapter()
        buttons.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    false
                }

                MotionEvent.ACTION_UP -> {
                    if (abs(downX - event.x) < slop
                        && abs(downY - event.y) < slop) {
                            finishWithAnimation()
                        true
                    } else {
                        false
                    }
                }

                else -> {
                    false
                }
            }
        }

        frame.setOnClickListener {
            finishWithAnimation()
        }
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