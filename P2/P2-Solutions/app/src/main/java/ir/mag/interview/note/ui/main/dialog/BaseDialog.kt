package ir.mag.interview.note.ui.main.dialog

abstract class BaseDialog {

    abstract val dialog: CommonDialog

    open fun show(){
        dialog.show()
    }

}