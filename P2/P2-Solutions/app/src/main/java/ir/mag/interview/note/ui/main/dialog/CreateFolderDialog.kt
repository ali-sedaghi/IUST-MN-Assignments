package ir.mag.interview.note.ui.main.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import ir.mag.interview.note.R

class CreateFolderDialog
constructor(
    private val lifecycleOwner: LifecycleOwner,
    val context: Context,
    private val handler: OnCreateCallback
) : BaseDialog() {

    override val dialog: CommonDialog
        get() =
            CommonDialog.Builder(lifecycleOwner, context)
                .setTitle(context.getString(R.string.new_folder))
                .setDescription(context.getString(R.string.new_folder_desctiption))
                .setConfirmText(context.getString(R.string.create_new_folder))
                .setListener(object : CommonDialog.OnHandle {
                    override fun onCancel(dialog: AlertDialog, text: String) {
                        dialog.dismiss()
                    }

                    override fun onConfirm(dialog: AlertDialog, text: String) {
                        handler.onCreate(dialog, text)
                    }
                })
                .setHasPrompt(true)
                .setPromptHint(context.getString(R.string.folder_title))
                .build()

    interface OnCreateCallback {
        fun onCreate(dialog: AlertDialog, text: String)
    }

}