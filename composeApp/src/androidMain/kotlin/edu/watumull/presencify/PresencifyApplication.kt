package edu.watumull.presencify

import android.app.Application
import qrgenerator.AppContext

class PresencifyApplication : Application() {
    companion object {
        lateinit var INSTANCE: PresencifyApplication
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AppContext.apply { set(applicationContext) }
    }
}
