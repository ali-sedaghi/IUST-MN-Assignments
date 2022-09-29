package ir.mag.interview.note.ui.editor

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ir.mag.interview.note.R
import ir.mag.interview.note.databinding.FragmentNoteEditorBinding
import ir.mag.interview.note.ui.NotesMainActivity
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [EditorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditorFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private val viewModel: EditorViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding: FragmentNoteEditorBinding


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as NotesMainActivity).notesComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        focusTitle()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.fragment_note_editor,
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
        // NOTE: think about it later
        // observe note to change edit note value and update the creation date
        viewModel.currentNote.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.noteEditorTitle.text = SpannableStringBuilder(it.title)
                binding.noteEditorContent.text = SpannableStringBuilder(it.content)
                val persianDate = PersianDate(it.creationDate)
                val formatter = PersianDateFormat("d F Y")
                binding.noteEditorDate.text = formatter.format(persianDate)
                viewModel.editedNote.postValue(it)
            }
        })
    }

    private fun setupUI() {
        // set listeners for editors
        binding.noteEditorTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.editedNote.value?.let {
                    val editedNote = it
                    editedNote.title = s.toString()
                    viewModel.editedNote.postValue(editedNote)
                }
            }
        })
        binding.noteEditorContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.editedNote.value?.let {
                    val editedNote = it
                    editedNote.content = s.toString()
                    viewModel.editedNote.postValue(editedNote)
                }
            }
        })

        // set auto focus
        focusTitle()
        binding.noteEditorTitle.setOnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                focusContent()
            }
            false
        }
    }

    private fun focusContent() {
        binding.noteEditorContent.requestFocus()
        binding.noteEditorContent.setSelection(binding.noteEditorContent.text.toString().length)
    }

    private fun focusTitle() {
        binding.noteEditorTitle.requestFocus()
        binding.noteEditorTitle.setSelection(binding.noteEditorTitle.text.toString().length)
    }


    companion object {
        private const val TAG = "Ui.NoteEditorFragment";
    }
}