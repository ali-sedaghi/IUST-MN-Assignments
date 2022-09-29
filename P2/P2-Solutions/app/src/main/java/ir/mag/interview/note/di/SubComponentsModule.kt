package ir.mag.interview.note.di

import dagger.Module
import ir.mag.interview.note.di.notes.NotesComponent

@Module(
    subcomponents = [
        NotesComponent::class
    ]
)
object SubComponentsModule {}