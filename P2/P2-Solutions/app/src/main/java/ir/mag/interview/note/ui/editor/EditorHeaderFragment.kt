package ir.mag.interview.note.ui.editor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ir.mag.interview.note.R
import ir.mag.interview.note.data.model.file.File
import ir.mag.interview.note.data.repository.NoteRepository
import ir.mag.interview.note.database.entity.note.Note
import ir.mag.interview.note.databinding.HeaderEditorActionBarBinding
import ir.mag.interview.note.ui.NotesMainActivity
import ir.mag.interview.note.ui.main.dialog.CommonDialog
import ir.mag.interview.note.ui.main.dialog.DeleteNoteDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditorHeaderFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private val viewModel: EditorViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding: HeaderEditorActionBarBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as NotesMainActivity).notesComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.header_editor_action_bar,
            container,
            false
        )
        binding.lifecycleOwner = this

        setupUI()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupUI() {
        // set on click handler for header buttons
        binding.editorHeaderBack.setOnClickListener {
            GlobalScope.launch {
                viewModel.updateNewNote()
            }
            viewModel.currentFolder.value?.let {
                if (it.folderId == NoteRepository.ROOT_FOLDER_ID) {
                    viewModel.goBackToBrowserMode()
                } else {
                    viewModel.goBackToInFolderBrowserMode()
                }
            }
        }

        binding.editorHeaderMore.setOnClickListener {
            viewModel.currentNote.value?.let {
                showMenu(binding.root, R.menu.note_file_more_menu, it)
            }
        }
    }

    //In the showMenu function from the previous example:
    private fun showMenu(v: View, @MenuRes menuRes: Int, file: File) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        setupIcons(popup)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {

                R.id.delete_note -> {
                    DeleteNoteDialog(this, requireContext(),
                        object : DeleteNoteDialog.OnDeleteCallback {
                            override fun onDelete(dialog: AlertDialog, text: String) {
                                GlobalScope.launch {
                                    viewModel.deleteNote(file as Note)

                                    viewModel.currentFolder.value?.let {
                                        if (it.folderId == NoteRepository.ROOT_FOLDER_ID) {
                                            viewModel.postGoBackToBrowser()
                                        } else {
                                            viewModel.postGoBackToInFolderBrowserMode()
                                        }
                                    }
                                }
                                dialog.dismiss()
                            }
                        }).show()
                    true
                }

                else -> throw UnsupportedOperationException("there is not this item")
            }
        }

        popup.show()
    }


    @SuppressLint("RestrictedApi")
    private fun setupIcons(popup: PopupMenu) {
        if (popup.menu is MenuBuilder) {
            val menuBuilder = popup.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)
            for (item in menuBuilder.visibleItems) {
                val iconMarginPx =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics
                    )
                        .toInt()
                if (item.icon != null) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
                    } else {
                        item.icon =
                            object :
                                InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0) {
                                override fun getIntrinsicWidth(): Int {
                                    return intrinsicHeight + iconMarginPx + iconMarginPx
                                }
                            }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "Ui.NotesHeader";
    }
}