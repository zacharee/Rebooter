package tk.zwander.rebooter.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

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
)

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
)

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
)

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
)

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
)