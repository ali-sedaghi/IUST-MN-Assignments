package ir.mag.interview.note.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ir.mag.interview.note.R
import ir.mag.interview.note.data.model.file.File
import ir.mag.interview.note.database.entity.folder.Folder
import ir.mag.interview.note.databinding.HeaderInFolderActionBarBinding
import ir.mag.interview.note.ui.NotesMainActivity
import ir.mag.interview.note.ui.editor.EditorHeaderFragment
import ir.mag.interview.note.ui.editor.EditorViewModel
import ir.mag.interview.note.ui.main.dialog.CommonDialog
import ir.mag.interview.note.ui.main.dialog.DeleteFolderDialog
import ir.mag.interview.note.ui.main.dialog.EditFolderDialog
import ir.mag.interview.note.ui.main.recycler.adapter.FilesRecyclerAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class InFolderHeaderFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private val viewModel: NotesViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding: HeaderInFolderActionBarBinding

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
            R.layout.header_in_folder_action_bar,
            container,
            false
        )
        binding.lifecycleOwner = this

        setupUI()

        observe()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun observe() {
        // observe the current directory name
        viewModel.currentFolder.observe(viewLifecycleOwner, Observer {
            binding.inFolderHeaderTitle.text = it.name
        })
    }

    private fun setupUI() {
        // setup on click listeners
        binding.inFolderHeaderBack.setOnClickListener {
            changeFolderToParentFolder()
        }
        binding.inFolderHeaderMore.setOnClickListener {
            viewModel.currentFolder.value?.let {
                showMenu(binding.root, R.menu.folder_file_more_menu, it)
            }
        }
    }

    private fun changeFolderToParentFolder() {
        viewModel.currentFolder.value?.let {
            viewModel.getParentFolder().observeOnce(this, Observer {
                it?.let {
                    viewModel.changeFolder(it)
                }
            })
        }
    }

    //In the showMenu function from the previous example:
    private fun showMenu(v: View, @MenuRes menuRes: Int, file: File) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        setupIcons(popup)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {

                R.id.edit_folder_name -> {
                    val folder = file as Folder
                    EditFolderDialog(this, requireContext(), folder,
                        object : EditFolderDialog.OnEditCallback {
                            override fun onEdit(dialog: AlertDialog, text: String) {
                                GlobalScope.launch {
                                    folder.name = text
                                    viewModel.updateFolder(folder)
                                    viewModel.postChangeFolder(folder)
                                }
                                dialog.dismiss()
                            }
                        }).show()
                    true
                }

                R.id.delete_folder -> {
                    DeleteFolderDialog(this, requireContext(), file as Folder,
                        object : DeleteFolderDialog.OnDeleteCallback {
                            override fun onDelete(dialog: AlertDialog, text: String) {
                                changeFolderToParentFolder()
                                GlobalScope.launch {
                                    viewModel.deleteFolder(file as Folder)
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
        private const val TAG = "Ui.InFolderHeader";
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}