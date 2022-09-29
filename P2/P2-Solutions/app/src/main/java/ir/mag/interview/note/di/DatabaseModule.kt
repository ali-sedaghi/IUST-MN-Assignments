package ir.mag.interview.note.di

import android.app.Application
import dagger.Module
import dagger.Provides
import ir.mag.interview.note.database.entity.note.NoteDao
import ir.mag.interview.note.database.entity.folder.FolderDao
import ir.mag.interview.note.database.FilesDatabase

@Module
object DatabaseModule {

    @JvmStatic
    @Provides
    fun provideNoteDao(
        context: Application
    ): NoteDao {
        return FilesDatabase.getDatabase(context).noteDao()
    }

    @JvmStatic
    @Provides
    fun provideFolderDao(
        context: Application
    ): FolderDao {
        return FilesDatabase.getDatabase(context).folderDao()
    }

}