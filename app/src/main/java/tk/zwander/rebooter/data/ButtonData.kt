package tk.zwander.rebooter.data

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.util.*

/**
 * The base class for a power button.
 * Each power button needs to have an icon
 * reference, a label reference, and two
 * colors for its background.
 *
 * Each parameter here is meant to take the name of
 * the appropriate resource. For instance, if the
 * icon is R.drawable.shut_down, the "icon" parameter
 * should be "shut_down".
 */
open class ButtonData(
    val icon: String,
    val name: String,
    val startColor: String,
    val endColor: String
) {
    /**
     * Keep loaded values stored for faster future loads.
     * These aren't persisted in preferences.
     */
    @Transient
    private var loadedIcon: Drawable? = null
    @Transient
    private var loadedName: String? = null
    @Transient
    private var loadedStartColor: Int? = null
    @Transient
    private var loadedEndColor: Int? = null

    override fun equals(other: Any?): Boolean {
        return other is ButtonData
                && icon == other.icon
                && name == other.name
                && startColor == other.startColor
                && endColor == other.endColor
    }

    override fun hashCode(): Int {
        return Objects.hash(icon, name, startColor, endColor)
    }

    /**
     * Load the icon for this button.
     */
    fun loadIcon(context: Context): Drawable {
        return loadedIcon ?: ContextCompat.getDrawable(
            context,
            context.resources.getIdentifier(
                icon,
                "drawable", context.packageName
            )
        )!!.apply {
            loadedIcon = this
        }
    }

    /**
     * Load the name/label for this button.
     */
    fun loadName(context: Context): String {
        return loadedName ?: context.resources.getString(
            context.resources.getIdentifier(
                name,
                "string", context.packageName
            )
        )
    }

    /**
     * Load the starting color for the background gradient
     * of this button.
     */
    fun loadStartColor(context: Context): Int {
        return loadedStartColor ?: ContextCompat.getColor(
            context,
            context.resources.getIdentifier(
                startColor,
                "color", context.packageName
            )
        )
    }

    /**
     * Load the ending color for the background gradient
     * of this button.
     */
    fun loadEndColor(context: Context): Int {
        return loadedEndColor ?: ContextCompat.getColor(
            context,
            context.resources.getIdentifier(
                endColor,
                "color", context.packageName
            )
        )
    }
}

/**
 * The reboot button adds a "reason"
 * field, which is passed to the power manager
 * command when it's pressed.
 */
class RebootButtonData(
    icon: String,
    name: String,
    startColor: String,
    endColor: String,
    val reason: String? = null
) : ButtonData(
    icon, name, startColor, endColor
) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
                && other is RebootButtonData
                && reason == other.reason
    }

    override fun hashCode(): Int {
        return Objects.hash(icon, name, startColor, endColor, reason)
    }
}

/**
 * The Safe Mode button has no extra
 * attributes. It's only a different
 * class for easy instance comparison.
 */
class SafeModeButtonData(
    icon: String,
    name: String,
    startColor: String,
    endColor: String
) : ButtonData(
    icon, name, startColor, endColor
) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
                && other is SafeModeButtonData
    }

    override fun hashCode(): Int {
        //No new parameters to compare
        return super.hashCode()
    }
}

/**
 * The Shut Down button is in the
 * same situation as the Safe Mode
 * button.
 */
class ShutDownButtonData(
    icon: String,
    name: String,
    startColor: String,
    endColor: String
) : ButtonData(
    icon, name, startColor, endColor
) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
                && other is ShutDownButtonData
    }

    override fun hashCode(): Int {
        //No new parameters to compare
        return super.hashCode()
    }
}

/**
 * A custom command button doesn't
 * necessarily reboot the device.
 *
 * The "command" attribute can be any
 * arbitrary command.
 */
class CustomCommandButtonData(
    icon: String,
    name: String,
    startColor: String,
    endColor: String,
    val command: String
) : ButtonData(
    icon, name, startColor, endColor
) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
                && other is CustomCommandButtonData
                && command == other.command
    }

    override fun hashCode(): Int {
        return Objects.hash(icon, name,
            startColor, endColor, command)
    }
}