package ir.mag.interview.note.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import ir.mag.interview.note.database.entity.note.Note
import ir.mag.interview.note.database.entity.folder.Folder

data class FolderWithNotes(
    @Embedded
    val folder: Folder,
    @Relation(
        parentColumn = "folderId",
        entityColumn = "folderId"
    )
    val notes: List<Note>
)