package tk.zwander.rebooter.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.util.*

/**
 * The base class for a power button.
 * Each power button needs to have an icon
 * reference, a label reference, and two
 * colors for its background.
 */
open class ButtonData(
    @DrawableRes val icon: Int,
    @StringRes val name: Int,
    @ColorRes val startColor: Int,
    @ColorRes val endColor: Int
) {
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
}

/**
 * The reboot button adds a "reason"
 * field, which is passed to the power manager
 * command when it's pressed.
 */
class RebootButtonData(
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes startColor: Int,
    @ColorRes endColor: Int,
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
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes startColor: Int,
    @ColorRes endColor: Int
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
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes startColor: Int,
    @ColorRes endColor: Int
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
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes startColor: Int,
    @ColorRes endColor: Int,
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
        return Objects.hash(icon, name, startColor, endColor, command)
    }
}