package tk.zwander.rebooter

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_main.*

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
                finishWithAnimation(intent.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            }
        }
    }

    private var runningFinishAnim = false

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

        buttons.adapter = ButtonAdapter()
        buttons.setOnTouchListener(SingleTapListener {
            finishWithAnimation()
        })

        frame.setOnTouchListener(SingleTapListener {
            finishWithAnimation()
        })

        registerReceiver(
            dismissReceiver,
            IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS).apply {
                addAction(Intent.ACTION_SCREEN_OFF)
            }
        )

        updateFlags()
    }

    override fun onBackPressed() {
        finishWithAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(dismissReceiver)
    }

    private fun finishWithAnimation(isForHome: Boolean = false) {
        if (isForHome) {
            finish()
            overridePendingTransition(0, R.anim.exit_anim)
        } else {
            if (!runningFinishAnim) {
                runningFinishAnim = true

                val anim = AnimationUtils.loadAnimation(this, R.anim.exit_anim)
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        runningFinishAnim = false
                        finish()
                    }
                })

                (window.decorView as ViewGroup).getChildAt(0).startAnimation(anim)
            }
        }
    }

    private fun updateFlags() {
        fun doNormalFlags() {
            window.setDimAmount(resources.getString(R.string.dim_amount).toFloat())
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        if (isTouchWiz) {
            try {
                window.attributes = window.attributes.apply {
                    val f = this::class.java
                        .getDeclaredField("samsungFlags")

                    f.set(this, f.get(this) as Int or 64)
                }
                window.setDimAmount(resources.getString(R.string.blur_amount).toFloat())
            } catch (e: Exception) {
                doNormalFlags()
            }
        } else {
            doNormalFlags()
        }
    }
}