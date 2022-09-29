package ir.mag.interview.note.di.notes

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ir.mag.interview.note.ui.NotesFragmentFactory

@Module
object NotesFragmentModule {

    @JvmStatic
    @NotesScope
    @Provides
    fun provideNotesFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): FragmentFactory {
        return NotesFragmentFactory(
            viewModelFactory
        )
    }
}