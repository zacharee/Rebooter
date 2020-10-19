package tk.zwander.rebooter

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    private val dismissReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS ||
                    intent?.action == Intent.ACTION_SCREEN_OFF) {
                finishWithAnimation()
            }
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

        registerReceiver(
            dismissReceiver,
            IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS).apply {
                addAction(Intent.ACTION_SCREEN_OFF)
            }
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finishWithAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(dismissReceiver)
    }

    private fun finishWithAnimation() {
        finish()
        overridePendingTransition(0, R.anim.exit_anim)
    }
}