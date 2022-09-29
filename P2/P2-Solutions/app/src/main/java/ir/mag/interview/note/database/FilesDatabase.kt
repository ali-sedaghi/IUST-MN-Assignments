package ir.mag.interview.note.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.icu.text.CaseMap
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import ir.mag.interview.note.database.entity.folder.Folder
import ir.mag.interview.note.database.entity.folder.FolderDao
import ir.mag.interview.note.database.entity.note.Note
import ir.mag.interview.note.database.entity.note.NoteDao

@Database(
    entities = [Folder::class, Note::class],
    version = 1,
    exportSchema = false
)
abstract class FilesDatabase : RoomDatabase() {

    abstract fun folderDao(): FolderDao

    abstract fun noteDao(): NoteDao

    class FilesDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            createRootFolder(db)
        }

        private fun createRootFolder(db: SupportSQLiteDatabase) {
            // create root folder
            val values = ContentValues().apply {
                put("folderId", 1L)
                put("parentFolderId", 0L)
                put("name", "/")
            }
            db.insert("folders", CONFLICT_IGNORE, values)
        }
    }


    companion object {

        @Volatile
        private var instance: FilesDatabase? = null

        fun getDatabase(context: Context): FilesDatabase {
            val returnedInstance =
                instance
            if (returnedInstance != null) {
                return returnedInstance
            }

            synchronized(this) {
                val tempInstance =
                    instance
                if (tempInstance != null) {
                    return tempInstance
                }

                // Create new object
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    FilesDatabase::class.java,
                    "files_database"
                ).addCallback(FilesDatabaseCallback()).build()
                instance = newInstance

                return newInstance
            }
        }
    }

}