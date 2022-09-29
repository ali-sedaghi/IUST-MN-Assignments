package ir.mag.interview.note.database.entity.folder

import androidx.lifecycle.LiveData
import androidx.room.*
import ir.mag.interview.note.database.entity.note.Note
import ir.mag.interview.note.database.relation.FolderWithNotes
import ir.mag.interview.note.database.relation.FolderWithSubFolders

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(folder: Folder)

    @Query("SELECT * FROM folders ORDER BY folderId DESC")
    fun selectAll(): LiveData<List<Folder>>

    @Query("SELECT * FROM folders WHERE folderId = :folderId ORDER BY folderId DESC")
    fun selectById(folderId: Long): LiveData<Folder>

    @Query("SELECT * FROM folders")
    fun readAllWithNotes(): LiveData<List<FolderWithNotes>>

    @Query("SELECT * FROM folders WHERE folderId = :folderId ORDER BY folderId DESC")
    fun readByIdWithNotes(folderId: Long): LiveData<FolderWithNotes>

    @Query("SELECT * FROM folders WHERE folderId = :folderId ORDER BY folderId DESC")
    fun readByIdWithSubFolders(folderId: Long): LiveData<FolderWithSubFolders>

    @Query("SELECT * FROM folders WHERE parentFolderId = :parentId ORDER BY folderId DESC")
    fun selectFoldersByParentId(parentId: Long): LiveData<List<Folder>>

    @Update
    suspend fun update(folder: Folder);

    @Delete
    suspend fun delete(folder: Folder)
}