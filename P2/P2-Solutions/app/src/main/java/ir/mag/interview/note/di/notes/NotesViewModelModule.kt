package ir.mag.interview.note.di.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ir.mag.interview.note.di.notes.keys.NotesViewModelKey
import ir.mag.interview.note.ui.NotesMainViewModel
import ir.mag.interview.note.ui.editor.EditorViewModel
import ir.mag.interview.note.ui.main.NotesViewModel
import ir.mag.interview.note.viewmodel.factory.NotesViewModelFactory

@Module
abstract class NotesViewModelModule {

    @NotesScope
    @Binds
    abstract fun bindNotesViewModelFactory(factory: NotesViewModelFactory): ViewModelProvider.Factory

    @NotesScope
    @Binds
    @IntoMap
    @NotesViewModelKey(NotesViewModel::class)
    abstract fun bindNotesViewModel(notesViewModel: NotesViewModel): ViewModel

    @NotesScope
    @Binds
    @IntoMap
    @NotesViewModelKey(EditorViewModel::class)
    abstract fun bindEditorViewModel(editorViewModel: EditorViewModel): ViewModel

    @NotesScope
    @Binds
    @IntoMap
    @NotesViewModelKey(NotesMainViewModel::class)
    abstract fun bindNotesMainViewModel(notesViewModel: NotesMainViewModel): ViewModel
}