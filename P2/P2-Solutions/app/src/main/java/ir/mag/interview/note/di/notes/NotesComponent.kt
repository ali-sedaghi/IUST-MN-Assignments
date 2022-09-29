package ir.mag.interview.note.di.notes

import dagger.Subcomponent
import ir.mag.interview.note.ui.NotesMainActivity
import ir.mag.interview.note.ui.editor.EditorFragment
import ir.mag.interview.note.ui.editor.EditorHeaderFragment
import ir.mag.interview.note.ui.main.InFolderHeaderFragment
import ir.mag.interview.note.ui.main.NotesFragment
import ir.mag.interview.note.ui.main.NotesHeaderFragment

@NotesScope
@Subcomponent(
    modules = [
        NotesModule::class,
        NotesFragmentModule::class,
        NotesViewModelModule::class
    ]
)
interface NotesComponent {

    @Subcomponent.Factory
    interface Factory {

        fun create(): NotesComponent
    }

    fun inject(activity: NotesMainActivity)
    fun inject(fragment: NotesFragment)
    fun inject(fragment: NotesHeaderFragment)
    fun inject(fragment: InFolderHeaderFragment)
    fun inject(fragment: EditorFragment)
    fun inject(fragment: EditorHeaderFragment)
}