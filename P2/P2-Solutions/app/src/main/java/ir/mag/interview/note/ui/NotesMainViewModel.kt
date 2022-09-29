package ir.mag.interview.note.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ir.mag.interview.note.data.repository.NoteRepository
import javax.inject.Inject

class NotesMainViewModel @Inject
constructor(
    noteRepository: NoteRepository
) : ViewModel() {
    var mode: LiveData<NoteRepository.Modes> = noteRepository.mode
}