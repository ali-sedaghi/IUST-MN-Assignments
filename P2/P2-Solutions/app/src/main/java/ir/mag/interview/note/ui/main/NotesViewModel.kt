package ir.mag.interview.note.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mag.interview.note.data.model.file.File
import ir.mag.interview.note.data.repository.NoteRepository
import ir.mag.interview.note.database.entity.folder.Folder
import ir.mag.interview.note.database.entity.note.Note
import ir.mag.interview.note.database.relation.FolderWithNotes
import ir.mag.interview.note.database.repository.NotesDatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.filter
import kotlin.collections.set
import kotlin.collections.sortedWith

/**
 * [NotesViewModel] is responsible for logic of main page
 * including the recycler and the headers.
 */
class NotesViewModel
@Inject
constructor(
    private val noteRepository: NoteRepository,
    private val notesDB: NotesDatabaseRepository
) : ViewModel() {

    var currentFolder: LiveData<Folder> = noteRepository.selectedFolder
    var currentNote: LiveData<Note> = noteRepository.selectedNote

    var allFolders: LiveData<List<Folder>> = notesDB.folders
    var allNotes: LiveData<List<Note>> = notesDB.notes

    var currentFiles: MediatorLiveData<EnumMap<File.Types, List<File>?>> = MediatorLiveData()

    private val dateComparator = Comparator { note1: Note, note2: Note ->
        note2.lastUpdateDate.compareTo(note1.lastUpdateDate)
    }

    fun setCurrentFilesSources() {
        Log.d(TAG, "setCurrentFilesSources: ")

        currentFiles.value = EnumMap(File.Types::class.java)
        currentFiles.value?.put(File.Types.FOLDER, ArrayList())
        currentFiles.value?.put(File.Types.NOTE, ArrayList())
        currentFiles.value?.let {

            // If notes change then files should change
            currentFiles.addSource(allNotes) { notesList ->
                Log.d(TAG, "setCurrentFilesSources: Notes has been changed")
                notesList?.let { notes ->
                    val newNotes = ArrayList(notes).filter {
                        it.folderId == currentFolder.value?.folderId
                    }
                    it[File.Types.NOTE] = newNotes.sortedWith(dateComparator)
                    currentFiles.postValue(it)
                }
            }

            // If folders change then files should change
            currentFiles.addSource(allFolders) { folderList ->
                Log.d(TAG, "setCurrentFilesSources: Folders has been changed")
                folderList?.let { folders ->
                    val newFolderList = ArrayList(folders).filter {
                        it.parentFolderId == currentFolder.value?.folderId
                    }
                    it[File.Types.FOLDER] = newFolderList
                    currentFiles.postValue(it)
                }
            }

            // If navigate another page then files should change
            currentFiles.addSource(currentFolder) { newCurrentFolder ->
                newCurrentFolder?.let { folder ->
                    val newNotes: List<Note>
                    val newFolders: List<Folder>
                    if (allFolders.value != null) {
                        newFolders = ArrayList(allFolders.value!!).filter {
                            it.parentFolderId == folder.folderId
                        }
                        it[File.Types.FOLDER] = newFolders
                    }
                    if (allNotes.value != null) {
                        newNotes = ArrayList(allNotes.value!!).filter {
                            it.folderId == folder.folderId
                        }
                        it[File.Types.NOTE] = newNotes.sortedWith(dateComparator)
                    }
                    currentFiles.postValue(it)
                }
            }
        }
    }


    /**
     * Database:
     *
     * [addFolder]
     * [addNote]
     * [updateFolder]
     * [deleteFolder]
     * [deleteNote]
     * [getFolderByIdWithNotes]
     * [getRootFolder]
     * [getParentFolder]
     */
    fun addFolder(folderName: String) {
        currentFolder.value?.let { folder ->
            viewModelScope.launch(Dispatchers.IO + NonCancellable) {
                notesDB.addFolder(Folder(0, folder.folderId, folderName))
            }
        }
    }

    fun addNote() {
        currentFolder.value?.let { folder ->
            viewModelScope.launch(Dispatchers.IO + NonCancellable) {
                val id = notesDB.addNote(Note(0, folder.folderId, "", "", Date(), Date()))
                val note = notesDB.getNoteByIdNow(id)
                noteRepository.selectedNote.postValue(note)
            }
        }
    }

    suspend fun updateFolder(folder: Folder) {
        notesDB.updateFolder(folder)
    }

    suspend fun deleteFolder(folder: Folder) {
        notesDB.deleteFolder(folder)
    }

    suspend fun deleteNote(note: Note) {
        notesDB.deleteNote(note)
    }

    fun getFolderByIdWithNotes(folderId: Long): LiveData<FolderWithNotes> {
        return notesDB.getFolderByIdWithNotes(folderId)
    }

    fun getRootFolder(): LiveData<Folder> {
        return notesDB.getFolderById(NoteRepository.ROOT_FOLDER_ID)
    }

    fun getParentFolder(): LiveData<Folder> {
        return notesDB.getFolderById(currentFolder.value!!.parentFolderId!!)
    }


    /**
     * Change state:
     *
     * [changeModeToInFolderBrowsing]
     * [changeModeToNormalBrowsing]
     * [goToEditPage]
     */
    fun changeModeToInFolderBrowsing() {
        noteRepository.changeMode(NoteRepository.Modes.IN_FOLDER_BROWSING)
    }

    fun changeModeToNormalBrowsing() {
        noteRepository.changeMode(NoteRepository.Modes.BROWSER)
    }

    fun goToEditPage(note: Note) {
        noteRepository.changeCurrentNote(note)
        noteRepository.changeMode(NoteRepository.Modes.EDITOR)
    }

    fun changeFolder(folder: Folder) {
        noteRepository.changeCurrentFolder(folder)
    }

    fun postChangeFolder(folder: Folder) {
        noteRepository.postChangeCurrentFolder(folder)
    }

    companion object {
        private const val TAG = "ViewModel.Notes"
    }
}