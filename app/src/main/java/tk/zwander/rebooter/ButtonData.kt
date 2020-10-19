package tk.zwander.rebooter

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

open class ButtonData(
    @DrawableRes val icon: Int,
    @StringRes val name: Int,
    @ColorRes val color: Int
)

class RebootButtonData(
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes color: Int,
    val reason: String? = null
) : ButtonData(
    icon, name, color
)

class SafeModeButtonData(
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes color: Int
) : ButtonData(
    icon, name, color
)

class ShutDownButtonData(
    @DrawableRes icon: Int,
    @StringRes name: Int,
    @ColorRes color: Int
) : ButtonData(
    icon, name, color
)