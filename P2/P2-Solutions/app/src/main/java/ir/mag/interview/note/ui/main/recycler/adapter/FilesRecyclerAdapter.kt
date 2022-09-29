package ir.mag.interview.note.ui.main.recycler.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.text.SpannableStringBuilder
import android.util.Log
import android.util.TimeUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import ir.mag.interview.note.R
import ir.mag.interview.note.data.model.file.File
import ir.mag.interview.note.database.entity.folder.Folder
import ir.mag.interview.note.database.entity.note.Note
import ir.mag.interview.note.databinding.FileViewHolderBinding
import ir.mag.interview.note.databinding.FragmentDialogCommonBinding
import ir.mag.interview.note.ui.main.NotesViewModel
import ir.mag.interview.note.ui.main.dialog.CommonDialog
import ir.mag.interview.note.ui.main.dialog.DeleteFolderDialog
import ir.mag.interview.note.ui.main.dialog.DeleteNoteDialog
import ir.mag.interview.note.ui.main.dialog.EditFolderDialog
import ir.mag.interview.note.util.TimeUtil
import kotlinx.coroutines.*
import kotlin.collections.ArrayList

class FilesRecyclerAdapter
constructor(
    val notesViewModel: NotesViewModel
) : RecyclerView.Adapter<FilesRecyclerAdapter.FileViewHolder>() {

    private lateinit var activity: AppCompatActivity

    var files: List<File> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        Log.d(TAG, "onCreateViewHolder: ")
        activity = parent.context as AppCompatActivity

        val binding: FileViewHolderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.file_view_holder,
            parent,
            false
        )

        return FileViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
//        Log.d(TAG, "onBindViewHolder: ")
        holder.bind(files[position])
    }

    companion object {
        private const val TAG = "Adapter.FilesRecycler"
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    inner class FileViewHolder(private val binding: FileViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(file: File) {

            when (file.type) {
                File.Types.NOTE -> {
                    bindNote(file as Note)
                }

                File.Types.FOLDER -> {
                    bindFolder(file as Folder)
                }
            }

        }

        private fun bindFolder(folder: Folder) {
            binding.fileCardTitle.text = folder.name
            notesViewModel.getFolderByIdWithNotes(folder.folderId)
                .observeOnce(activity, Observer {
                    it?.notes?.let { notes ->
                        binding.fileCardDescription.text =
                            activity.getString(R.string.count_notes, notes.size)
                    }
                })
            binding.fileCardIconFrame.background =
                ContextCompat.getDrawable(activity, R.drawable.circle_yellow);
            binding.fileCardIcon.setImageResource(R.drawable.ic_folder)
            binding.fileCard.setOnClickListener {
                Log.d(TAG, "bind onclick card: ")
                notesViewModel.changeFolder(folder)
            }
            binding.fileCardOptionButton.setOnClickListener {
                Log.d(TAG, "bind onclick more button: ")
                it?.let {
                    showMenu(it, R.menu.folder_file_more_menu, folder)
                }
            }
        }

        private fun bindNote(note: Note) {
            binding.fileCardTitle.text = note.title
            bindLastVisitDate(TimeUtil.getDistanceDescription(note.lastUpdateDate))
            binding.fileCardIconFrame.background =
                ContextCompat.getDrawable(activity, R.drawable.circle_light_blue);
            binding.fileCardIcon.setImageResource(R.drawable.ic_note)
            binding.fileCard.setOnClickListener {
                notesViewModel.goToEditPage(note)
            }
            binding.fileCardOptionButton.setOnClickListener {
                it?.let {
                    showMenu(it, R.menu.note_file_more_menu, note)
                }
            }
        }

        private fun bindLastVisitDate(distanceDescription: TimeUtil.TimeDifference) {
            val distance = activity.resources.getString(
                R.string.last_visit,
                distanceDescription.amount.toInt(),
                TimeUtil.typeToPersianString(distanceDescription)
            )
            binding.fileCardDescription.text = distance
        }

        //In the showMenu function from the previous example:
        private fun showMenu(v: View, @MenuRes menuRes: Int, file: File) {
            val popup = PopupMenu(activity, v)
            popup.menuInflater.inflate(menuRes, popup.menu)

            setupIcons(popup)

            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.edit_folder_name -> {

                        val folder = file as Folder
                        EditFolderDialog(activity, activity, folder,
                            object : EditFolderDialog.OnEditCallback {
                                override fun onEdit(dialog: AlertDialog, text: String) {
                                    GlobalScope.launch {
                                        folder.name = text
                                        notesViewModel.updateFolder(folder)
                                    }
                                    dialog.dismiss()
                                }
                            }).show()
                        true
                    }

                    R.id.delete_folder -> {
                        DeleteFolderDialog(activity, activity, file as Folder,
                            object : DeleteFolderDialog.OnDeleteCallback {
                                override fun onDelete(dialog: AlertDialog, text: String) {
                                    GlobalScope.launch {
                                        notesViewModel.deleteFolder(file)
                                    }
                                    dialog.dismiss()
                                }
                            }).show()
                        true
                    }

                    R.id.delete_note -> {
                        DeleteNoteDialog(
                            activity,
                            activity,
                            object : DeleteNoteDialog.OnDeleteCallback {
                                override fun onDelete(dialog: AlertDialog, text: String) {
                                    GlobalScope.launch {
                                        notesViewModel.deleteNote(file as Note)
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
                            TypedValue.COMPLEX_UNIT_DIP, 20f, activity.resources.displayMetrics
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
    }

}