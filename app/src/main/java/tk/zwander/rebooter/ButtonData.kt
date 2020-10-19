package tk.zwander.rebooter

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

open class ButtonData(
    @DrawableRes val icon: Int,
    @StringRes val name: Int,
    @ColorRes val startColor: Int,
    @ColorRes val endColor: Int
)

class RebootButtonData(
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes startColor: Int,
    @ColorRes endColor: Int,
    val reason: String? = null
) : ButtonData(
    icon, name, startColor, endColor
)

class SafeModeButtonData(
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes startColor: Int,
    @ColorRes endColor: Int
) : ButtonData(
    icon, name, startColor, endColor
)

class ShutDownButtonData(
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes startColor: Int,
    @ColorRes endColor: Int
) : ButtonData(
    icon, name, startColor, endColor
)

class CustomCommandButtonData(
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes startColor: Int,
    @ColorRes endColor: Int,
    val command: String
) : ButtonData(
    icon, name, startColor, endColor
)