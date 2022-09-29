package ir.mag.interview.note.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.mag.interview.note.R
import ir.mag.interview.note.ui.NotesMainActivity
import javax.inject.Inject

class NotesHeaderFragment
@Inject
constructor() : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as NotesMainActivity).notesComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.header_normal_action_bar, container, false)
    }

    companion object {
        private const val TAG = "Ui.NotesHeader";
    }
}