package tk.zwander.rebooter.ui

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.add_button_dialog.*
import tk.zwander.rebooter.R
import tk.zwander.rebooter.data.ButtonData

/**
 * The dialog containing the list of buttons that can be
 * added to the main list.
 */
class AddButtonDialog(
    context: Context,
    private val items: List<ButtonData>,
    private val selectionCallback: (ButtonData) -> Unit
) : MaterialAlertDialogBuilder(context, R.style.Theme_Rebooter_Dialog) {
    init {
        setTitle(R.string.add_button)
        setPositiveButton(android.R.string.cancel, null)
        setView(R.layout.add_button_dialog)
        background = ContextCompat.getDrawable(context,
            R.drawable.add_button_dialog_background)
    }

    override fun create(): AlertDialog {
        return super.create().also {
            //Make sure the proper animations are applied,
            //since the dialogTheme style attributes aren't reliable.
            it.window?.attributes
                ?.windowAnimations = R.style.Theme_Rebooter_Dialog
        }
    }

    override fun show(): AlertDialog {
        return super.show().also { dialog ->
            //Initialize the adapter with the available
            //buttons.
            val adapter = AddButtonAdapter(context) {
                selectionCallback(it)
                dialog.dismiss()
            }
            adapter.setItems(items)

            //Set the adapter with a slight delay to
            //allow the open animation to play.
            dialog.buttons_list.postDelayed({
                dialog.buttons_list.adapter = adapter
            }, 100)
        }
    }
}