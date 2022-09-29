package ir.mag.interview.note.database.repository

import androidx.lifecycle.LiveData
import ir.mag.interview.note.database.entity.folder.Folder
import ir.mag.interview.note.database.entity.folder.FolderDao
import ir.mag.interview.note.database.entity.note.Note
import ir.mag.interview.note.database.entity.note.NoteDao
import ir.mag.interview.note.database.relation.FolderWithNotes
import ir.mag.interview.note.database.relation.FolderWithSubFolders
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesDatabaseRepository
@Inject
constructor(
    private val noteDao: NoteDao,
    private val folderDao: FolderDao
) {

    val notes: LiveData<List<Note>> = noteDao.selectAll()
    val folders: LiveData<List<Folder>> = folderDao.selectAll()

    /**
     * Notes
     */
    suspend fun addNote(note: Note): Long {
        return noteDao.insert(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.update(note)
    }

    fun getNoteByIdNow(noteId: Long): Note {
        return noteDao.selectByIdNow(noteId)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    /**
     * Folders
     */
    suspend fun addFolder(folder: Folder) {
        folderDao.insert(folder)
    }

    suspend fun updateFolder(folder: Folder) {
        folderDao.update(folder)
    }


    suspend fun deleteFolder(folder: Folder) {
        folderDao.delete(folder)
    }

    fun getFolderByIdWithNotes(folderId: Long): LiveData<FolderWithNotes> {
        return folderDao.readByIdWithNotes(folderId)
    }

    fun getFolderById(folderId: Long): LiveData<Folder> {
        return folderDao.selectById(folderId)
    }

    companion object {
        const val TAG: String = "Repository.NotesDatabase"
    }
}