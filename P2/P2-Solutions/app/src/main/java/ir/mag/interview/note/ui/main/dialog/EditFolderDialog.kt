package ir.mag.interview.note.ui.main.dialog

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import ir.mag.interview.note.R
import ir.mag.interview.note.database.entity.folder.Folder


class EditFolderDialog
constructor(
    private val lifecycleOwner: LifecycleOwner,
    val context: Context,
    private val folder: Folder,
    private val handler: OnEditCallback
) : BaseDialog() {

    override val dialog: CommonDialog
        get() =
            CommonDialog.Builder(lifecycleOwner, context)
                .setTitle(context.resources.getString(R.string.edit_folder_name))
                .setConfirmText(context.resources.getString(R.string.save))
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
                        handler.onEdit(dialog, text)
                    }
                })
                .setHasPrompt(true)
                .setPromptText(SpannableStringBuilder(folder.name))
                .build()

    interface OnEditCallback {
        fun onEdit(dialog: AlertDialog, text: String)
    }

}
