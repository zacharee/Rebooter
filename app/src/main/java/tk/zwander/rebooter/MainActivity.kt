package tk.zwander.rebooter

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.rainbow.Rainbow
import com.skydoves.rainbow.RainbowOrientation
import com.skydoves.rainbow.contextColor
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_main.*
import tk.zwander.rebooter.ui.AddButtonDialog
import tk.zwander.rebooter.ui.ButtonAdapter
import tk.zwander.rebooter.util.SingleTapListener
import tk.zwander.rebooter.util.isTouchWiz
import tk.zwander.rebooter.util.prefManager

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
                intent?.action == Intent.ACTION_SCREEN_OFF
            ) {
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
        val adapter = ButtonAdapter { adapter, _ ->
            prefManager.setPowerButtons(adapter.items)
        }
        adapter.setItems(prefManager.getPowerButtons())
        buttons.adapter = adapter

        val touchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    adapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
                    adapter.selectedIndex = -1
                    prefManager.setPowerButtons(adapter.items)
                    return true
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                        val index = viewHolder?.adapterPosition ?: -1

                        if (adapter.selectedIndex != index) {
                            adapter.selectedIndex = index
                        } else {
                            adapter.selectedIndex = -1
                        }

                        viewHolder?.itemView?.alpha = 0.5f
                    }

                    super.onSelectedChanged(viewHolder, actionState)
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    viewHolder.itemView.alpha = 1.0f

                    super.clearView(recyclerView, viewHolder)
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            }
        )
        touchHelper.attachToRecyclerView(buttons)

        //If the RecyclerView or the frame itself is tapped,
        //dismiss the menu.
        buttons.setOnTouchListener(SingleTapListener {
            finishWithAnimation()
        })
        frame.setOnTouchListener(SingleTapListener {
            finishWithAnimation()
        })

        var previousNonZeroInsets: WindowInsetsCompat? = null

        //Weird hacky stuff because normal window insetting doesn't work for
        //whatever reason.
        ViewCompat.setOnApplyWindowInsetsListener(frame) { v, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            if (systemInsets.left > 0 || systemInsets.top > 0 || systemInsets.right > 0 || systemInsets.bottom > 0) {
                previousNonZeroInsets = WindowInsetsCompat(insets)
                insets
            } else {
                previousNonZeroInsets ?: insets
            }.also {
                val properInsets = it.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(properInsets.left, properInsets.top, properInsets.right, properInsets.bottom)
            }
        }

        add_button.setOnClickListener {
            AddButtonDialog(this, prefManager.defaultButtons - adapter.items) {
                adapter.addItem(it)
                prefManager.setPowerButtons(adapter.items)
            }.show()
        }

        add_background.setImageDrawable(
            Rainbow(add_background).palette {
                +contextColor(R.color.add_1)
                +contextColor(R.color.add_2)
            }.withAlpha(255)
                .getDrawable(RainbowOrientation.DIAGONAL_TOP_LEFT)
        )

        Rainbow(button_bar_background).palette {
            +contextColor(R.color.button_bar_1)
            +contextColor(R.color.button_bar_2)
        }.withAlpha(200)
            .background(RainbowOrientation.DIAGONAL_TOP_LEFT)

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
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR)

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