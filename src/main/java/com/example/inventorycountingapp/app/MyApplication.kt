package com.example.inventorycountingapp.app

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    companion object {
        lateinit var myApplication: MyApplication
            private set

        @JvmStatic
        val appContext: Context
            get() = myApplication.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        myApplication = this
    }

}
