package tk.zwander.rebooter

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_main.*
import tk.zwander.rebooter.ui.ButtonAdapter
import tk.zwander.rebooter.util.SingleTapListener
import tk.zwander.rebooter.util.isTouchWiz

/**
 * The power dialog itself.
 */
class MainActivity : AppCompatActivity() {
    companion object {
        init {
            //Do some shell setup.
            Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_REDIRECT_STDERR)
                    .setTimeout(10)
            )
        }
    }

    /**
     * To better show dismiss animations, and to properly dismiss on screen-off,
     * receive ACTION_CLOSE_SYSTEM_DIALOGS and ACTION_SCREEN_OFF.
     */
    private val dismissReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS ||
                    intent?.action == Intent.ACTION_SCREEN_OFF) {
                finishWithAnimation(intent.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            }
        }
    }

    /**
     * A flag to avoid sequential runs of the closing
     * animation.
     */
    private var runningFinishAnim = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //We want the Activity to show on the lock screen.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            setShowWhenLocked(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }

        //Set up the RecyclerView and Adapter.
        buttons.adapter = ButtonAdapter()

        //If the RecyclerView or the frame itself is tapped,
        //dismiss the menu.
        buttons.setOnTouchListener(SingleTapListener {
            finishWithAnimation()
        })
        frame.setOnTouchListener(SingleTapListener {
            finishWithAnimation()
        })

        //Register the receiver.
        registerReceiver(
            dismissReceiver,
            IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS).apply {
                addAction(Intent.ACTION_SCREEN_OFF)
            }
        )

        //Make sure the window has the proper flags.
        updateFlags()
    }

    override fun onBackPressed() {
        //There's no back-button logic, so just close
        //with the proper animation.
        finishWithAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()

        //Clean up.
        unregisterReceiver(dismissReceiver)
    }

    /**
     * Finish the Activity, but play an animation first.
     * Android is weirdly inconsistent with exit animations.
     * If the device is on the home screen, it's enough to just
     * override the pending transition. Otherwise, we have to manually
     * run the exit animation and then finish.
     */
    private fun finishWithAnimation(isForHome: Boolean = false) {
        if (isForHome) {
            finish()
            overridePendingTransition(0, R.anim.exit_anim)
        } else {
            //Only run if we aren't already.
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

                //It isn't possible to animate the DecorView directly,
                //so grab the next-best thing.
                (window.decorView as ViewGroup).getChildAt(0).startAnimation(anim)
            }
        }
    }

    /**
     * Set some custom flags and whatnot for the window.
     * On Samsung devices, this will add a background blur.
     * On other devices, it will set a background dim.
     *
     * On all devices, it makes sure the window shows behind the
     * status and navigation bars.
     */
    private fun updateFlags() {
        fun doNormalFlags() {
            window.setDimAmount(resources.getString(R.string.dim_amount).toFloat())
        }

        //Samsung uses DIM_BEHIND and dimAmount to enable and set the
        //blur, for whatever reason, so it needs to be enabled here.
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        //Make sure the window shows behind the system bars.
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        if (isTouchWiz && resources.getBoolean(R.bool.allow_blur)) {
            //This is a Samsung device and the resource value allows us to
            //blur. Add the super secret samsungFlag to enable blur, and
            //set the dimAmount, which is actually the blur amount.
            try {
                window.attributes = window.attributes.apply {
                    val f = this::class.java
                        .getDeclaredField("samsungFlags")

                    f.set(this, f.get(this) as Int or 64)
                }
                window.setDimAmount(resources.getString(R.string.blur_amount).toFloat())
            } catch (e: Exception) {
                //If something went wrong, just do a normal dim.
                doNormalFlags()
            }
        } else {
            //This isn't a Samsung device, or the resource value is "false".
            //Do a normal dim.
            doNormalFlags()
        }
    }
}