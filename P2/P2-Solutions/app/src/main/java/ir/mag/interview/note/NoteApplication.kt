package ir.mag.interview.note

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ir.mag.interview.note.di.ApplicationComponent
import ir.mag.interview.note.di.DaggerApplicationComponent


class NoteApplication : Application() {
    companion object {
        private lateinit var context: Context
    }

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        applicationComponent = DaggerApplicationComponent
            .builder()
            .application(this)
            .build()
    }
}