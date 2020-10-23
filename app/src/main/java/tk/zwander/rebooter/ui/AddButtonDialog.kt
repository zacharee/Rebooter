package tk.zwander.rebooter.ui

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.add_button_dialog.*
import kotlinx.android.synthetic.main.add_button_dialog.view.*
import tk.zwander.rebooter.R
import tk.zwander.rebooter.data.ButtonData

class AddButtonDialog(
    context: Context,
    private val items: List<ButtonData>,
    private val selectionCallback: (ButtonData) -> Unit
) : MaterialAlertDialogBuilder(context) {
    init {
        setTitle(R.string.add_button)
        setPositiveButton(android.R.string.cancel, null)
        setView(R.layout.add_button_dialog)
    }

    override fun show(): AlertDialog {
        return super.show().also { dialog ->
            val adapter = AddButtonAdapter(context) {
                selectionCallback(it)
                dialog.dismiss()
            }
            adapter.setItems(items)

            dialog.buttons_list.adapter = adapter
        }
    }
}