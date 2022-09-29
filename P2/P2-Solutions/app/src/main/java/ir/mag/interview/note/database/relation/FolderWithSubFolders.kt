package ir.mag.interview.note.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import ir.mag.interview.note.database.entity.folder.Folder

data class FolderWithSubFolders(
    @Embedded
    val folder: Folder,
    @Relation(
        parentColumn = "folderId",
        entityColumn = "parentFolderId"
    )
    val subFolders: List<Folder>
)