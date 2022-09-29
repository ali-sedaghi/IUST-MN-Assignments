package ir.mag.interview.note.ui.main.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import ir.mag.interview.note.R

class DeleteNoteDialog
constructor(
    private val lifecycleOwner: LifecycleOwner,
    val context: Context,
    private val handler: OnDeleteCallback
) : BaseDialog() {

    override val dialog: CommonDialog
        get() =
            CommonDialog.Builder(lifecycleOwner, context)
                .setTitle(context.getString(R.string.delete_note))
                .setDescription(context.getString(R.string.delete_note_description))
                .setConfirmText(context.getString(R.string.delete))
                .setListener(object : CommonDialog.OnHandle {
                    override fun onCancel(
                        dialog: AlertDialog,
                        text: String
                    ) {
                        dialog.dismiss()
                    }

                    override fun onConfirm(
                        dialog: AlertDialog,
                        text: String
                    ) {
                        handler.onDelete(dialog, text)
                    }
                })
                .build()

    interface OnDeleteCallback {
        fun onDelete(dialog: AlertDialog, text: String)
    }

}
