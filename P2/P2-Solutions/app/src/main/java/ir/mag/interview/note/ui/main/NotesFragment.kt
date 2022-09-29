package ir.mag.interview.note.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ir.mag.interview.note.R
import ir.mag.interview.note.data.model.file.File
import ir.mag.interview.note.data.repository.NoteRepository
import ir.mag.interview.note.databinding.FragmentNotesBinding
import ir.mag.interview.note.ui.NotesMainActivity
import ir.mag.interview.note.ui.main.dialog.CreateFolderDialog
import ir.mag.interview.note.ui.main.recycler.adapter.FilesRecyclerAdapter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [NotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotesFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private val viewModel: NotesViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding: FragmentNotesBinding

    @Inject
    lateinit var filesRecyclerAdapter: FilesRecyclerAdapter

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
            R.layout.fragment_notes,
            container,
            false
        )
        binding.lifecycleOwner = this

        // check if no folder is selected
        // for bug fix v0.1.1
        if (viewModel.currentFolder.value == null || viewModel.currentFiles.value == null) {
            viewModel.getRootFolder().observeOnce(this, Observer {
                it?.let { folder ->
                    Log.d(
                        TAG,
                        "onCreateView current folder changed root founded: ${folder.folderId}"
                    )
                    viewModel.changeFolder(folder)
                    viewModel.setCurrentFilesSources()
                }
            })
        }

        setupUI()
        observe()

        // Inflate the layout for this fragment
        return binding.root
    }


    private fun observe() {
        // observe the folder to change the mode
        viewModel.currentFolder.observe(viewLifecycleOwner, Observer {
            it?.let { folder ->
                Log.d(TAG, "onCreateView current folder changed: ${folder.folderId}")
                if (folder.folderId == NoteRepository.ROOT_FOLDER_ID) {
                    viewModel.changeModeToNormalBrowsing()
                } else {
                    viewModel.changeModeToInFolderBrowsing()
                }
            }
        })

        // observe files list that is connected to database
        viewModel.currentFiles.observe(viewLifecycleOwner, Observer {
            it?.let {
                updateRecycler(it)
            }
        })
    }

    private fun updateRecycler(files: EnumMap<File.Types, List<File>?>) {
        Log.d(TAG, "setupUI: in the current files observer")

        val newFiles = ArrayList<File>()
        files[File.Types.FOLDER]?.let { list ->
            newFiles.addAll(list)
        }
        files[File.Types.NOTE]?.let { list ->
            newFiles.addAll(list)
        }

        filesRecyclerAdapter.files = newFiles
        filesRecyclerAdapter.notifyDataSetChanged()
    }


    private fun setupUI() {
        // for bug fix v0.1.1
        viewModel.currentFiles.value?.let {
            updateRecycler(it)
        }

        // set adapter for files recycler
        binding.notesFilesList.adapter = filesRecyclerAdapter

        // floating action button
        binding.fabNewNote.setOnClickListener {
            binding.fab.collapse()
            viewModel.addNote()
            viewModel.currentNote.observeOnce(this, Observer {
                it?.let {
                    viewModel.goToEditPage(it)
                }
            })
        }

        binding.fabNewFolder.setOnClickListener {
            CreateFolderDialog(
                this,
                requireContext(),
                object : CreateFolderDialog.OnCreateCallback {
                    override fun onCreate(dialog: AlertDialog, text: String) {
                        if (text == "") {
                            viewModel.addFolder(resources.getString(R.string.unname))
                        } else {
                            viewModel.addFolder(text)
                        }
                        dialog.dismiss()
                    }
                }).show()
            binding.fab.collapse()
        }
    }

    companion object {
        private const val TAG = "Ui.NotesFragment";
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