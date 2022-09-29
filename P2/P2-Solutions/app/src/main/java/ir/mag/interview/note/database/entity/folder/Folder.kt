package ir.mag.interview.note.database.entity.folder

import androidx.annotation.NonNull
import androidx.room.*
import ir.mag.interview.note.data.model.file.File

@Entity(tableName = "folders")
class Folder(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    val folderId: Long,
    val parentFolderId: Long?,
    var name: String
) : File() {
    override val type: Types
        get() = Types.FOLDER
}