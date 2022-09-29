package ir.mag.interview.note.ui.main.dialog

import android.content.Context
import android.service.voice.VoiceInteractionSession
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import ir.mag.interview.note.R
import ir.mag.interview.note.databinding.FragmentDialogCommonBinding

class CommonDialog private constructor(
    val lifecycleOwner: LifecycleOwner,
    val context: Context,
    val title: String?,
    val description: String?,
    // Prompt
    val hasPrompt: Boolean = false,
    val promptHint: String?,
    val promptText: Editable?,
    // Buttons and Callbacks
    val listener: OnHandle?,
    val confirmText: String?,
    val cancelText: String?
) {

    lateinit var binding: FragmentDialogCommonBinding
    private lateinit var dialog: AlertDialog

    data class Builder(
        private var lifecycleOwner: LifecycleOwner,
        private var context: Context,
        private var title: String? = null,
        private var description: String? = null,
        private var hasPrompt: Boolean = false,
        private var promptHint: String? = null,
        private var promptText: Editable? = null,
        private var listener: OnHandle? = null,
        private var confirmText: String? = null,
        private var cancelText: String? = null
    ) {

        fun setTitle(title: String) = apply { this.title = title }
        fun setDescription(description: String) = apply { this.description = description }
        fun setHasPrompt(hasPrompt: Boolean) = apply { this.hasPrompt = hasPrompt }
        fun setPromptHint(promptHint: String) = apply { this.promptHint = promptHint }
        fun setPromptText(text: Editable) = apply { this.promptText = text }
        fun setListener(listener: OnHandle) = apply { this.listener = listener }
        fun setCancelText(cancelText: String) = apply { this.cancelText = cancelText }
        fun setConfirmText(confirmText: String) = apply { this.confirmText = confirmText }
        fun build() = CommonDialog(
            lifecycleOwner,
            context,
            title,
            description,
            hasPrompt,
            promptHint,
            promptText,
            listener,
            confirmText,
            cancelText
        )
    }

    fun show() {
        // inflate
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_dialog_common,
            null,
            false
        )
        binding.lifecycleOwner = lifecycleOwner


        // create and show the alert dialog
        dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(context.resources.getDrawable(R.drawable.dialog_bg))


        // set properties
        binding.confirmDialogTitle.text = title
        binding.confirmDialogAcceptButton.text = confirmText
        binding.commonDialogTextField.text = promptText
        cancelText?.let {
            binding.confirmDialogCancelButton.text = it
        }
        description?.let {
            binding.confirmDialogQuestion.visibility = View.VISIBLE
            binding.confirmDialogQuestion.text = description
        }

        // prompt
        if (hasPrompt) {
            binding.commonDialogTextFieldLayout.visibility = View.VISIBLE
            binding.commonDialogTextField.visibility = View.VISIBLE
            promptHint?.let { hint ->
                binding.commonDialogTextFieldLayout.hint = hint
            }
        }

        // listeners
        listener?.let { handler ->
            binding.confirmDialogAcceptButton.setOnClickListener {
                handler.onConfirm(dialog, binding.commonDialogTextField.text.toString())
            }
            binding.confirmDialogCancelButton.setOnClickListener {
                handler.onCancel(dialog, binding.commonDialogTextField.text.toString())
            }
        }

        dialog.show()
    }

    interface OnHandle {
        fun onCancel(dialog: AlertDialog, text: String)
        fun onConfirm(dialog: AlertDialog, text: String)
    }
}