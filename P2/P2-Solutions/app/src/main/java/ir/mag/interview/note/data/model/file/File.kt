package ir.mag.interview.note.data.model.file

abstract class File {
    enum class Types {
        FOLDER, NOTE
    }

    abstract val type: Types
}