package ru.vincetti.vimoney

import android.app.Application
import android.content.Context

class App : Application(){

    companion object{
        var context: Context? = null

        fun getAppContext(): Context? {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}