package ir.mag.interview.note.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ir.mag.interview.note.di.notes.NotesComponent
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        SubComponentsModule::class
    ]
)

interface ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }

    fun notesComponent(): NotesComponent.Factory
}