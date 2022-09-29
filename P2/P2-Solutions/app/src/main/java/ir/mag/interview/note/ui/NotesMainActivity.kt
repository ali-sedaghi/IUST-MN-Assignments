package ir.mag.interview.note.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import ir.mag.interview.note.NoteApplication
import ir.mag.interview.note.data.repository.NoteRepository
import ir.mag.interview.note.databinding.ActivityNotesMainBinding
import ir.mag.interview.note.di.notes.NotesComponent
import ir.mag.interview.note.ui.editor.EditorFragment
import ir.mag.interview.note.ui.editor.EditorHeaderFragment
import ir.mag.interview.note.ui.editor.EditorViewModel
import ir.mag.interview.note.ui.main.InFolderHeaderFragment
import ir.mag.interview.note.ui.main.NotesFragment
import ir.mag.interview.note.ui.main.NotesHeaderFragment
import ir.mag.interview.note.ui.main.NotesViewModel
import ir.mag.interview.note.util.UiUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.UnsupportedOperationException
import javax.inject.Inject

class NotesMainActivity : AppCompatActivity() {

    lateinit var notesComponent: NotesComponent

    @Inject
    lateinit var fragmentFactory: NotesFragmentFactory

    @Inject
    lateinit var notesMainViewModel: NotesMainViewModel

    @Inject
    lateinit var notesViewModel: NotesViewModel

    @Inject
    lateinit var editorViewModel: EditorViewModel

    lateinit var binding: ActivityNotesMainBinding

    // Fragments
    private lateinit var notesFragment: Fragment
    private lateinit var editorFragment: Fragment
    private lateinit var notesHeaderFragment: Fragment
    private lateinit var inFolderHeaderFragment: Fragment
    private lateinit var editorHeaderFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        inject()
        provideFragments()

        binding = ActivityNotesMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()

        handleUiModes()
    }

    override fun finish() {
        notesMainViewModel.mode.value?.let {
            when (it) {
                NoteRepository.Modes.BROWSER -> super.finish()
                NoteRepository.Modes.IN_FOLDER_BROWSING -> {
                    notesViewModel.currentFolder.value?.let {
                        notesViewModel.getParentFolder().observeOnce(this,
                            Observer { folder ->
                                notesViewModel.changeFolder(folder)
                            })
                    }
                }
                NoteRepository.Modes.EDITOR -> {
                    GlobalScope.launch {
                        editorViewModel.updateNewNote()
                    }
                    editorViewModel.currentFolder.value?.let { folder ->
                        if (folder.folderId == NoteRepository.ROOT_FOLDER_ID) {
                            editorViewModel.goBackToBrowserMode()
                        } else {
                            editorViewModel.goBackToInFolderBrowserMode()
                        }
                    }
                }
                else -> super.finish()
            }
        }
    }

    private fun provideFragments() {
        // main fragments
        notesFragment =
            fragmentFactory.instantiate(classLoader, NotesFragment::class.java.name)
        editorFragment =
            fragmentFactory.instantiate(classLoader, EditorFragment::class.java.name)

        // headers
        notesHeaderFragment =
            fragmentFactory.instantiate(classLoader, NotesHeaderFragment::class.java.name)
        inFolderHeaderFragment =
            fragmentFactory.instantiate(classLoader, InFolderHeaderFragment::class.java.name)
        editorHeaderFragment =
            fragmentFactory.instantiate(classLoader, EditorHeaderFragment::class.java.name)
    }

    private fun handleUiModes() {
        Log.d(TAG, "setupUI: ")

        notesMainViewModel.mode.observe(this, Observer {
            it?.let {
                when (it) {
                    NoteRepository.Modes.BROWSER -> {
                        Log.d(TAG, "setupUI: change to normal browser mode")
                        updateFragments(
                            notesHeaderFragment,
                            notesFragment
                        )
                    }

                    NoteRepository.Modes.IN_FOLDER_BROWSING -> {
                        Log.d(TAG, "setupUI: change to in folder browser mode")
                        updateFragments(
                            inFolderHeaderFragment,
                            notesFragment
                        )
                    }

                    NoteRepository.Modes.EDITOR -> {
                        Log.d(TAG, "setupUI: change to in editing mode")
                        updateFragments(
                            editorHeaderFragment,
                            editorFragment
                        )
                    }
                }
            }
        })

    }

    private fun updateFragments(header: Fragment, content: Fragment) {
        // header fragment
        UiUtil.changeFragment(
            supportFragmentManager,
            header,
            binding.header.id,
            true,
            header::class.java.name
        )
        // content fragment
        UiUtil.changeFragment(
            supportFragmentManager,
            content,
            binding.mainFrameLayout.id,
            true,
            content::class.java.name
        )
    }

    private fun inject() {
        notesComponent =
            (applicationContext as NoteApplication).applicationComponent
                .notesComponent()
                .create()
        notesComponent.inject(this)
    }

    companion object {
        private const val TAG = "Activity.NotesMainActivity"
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