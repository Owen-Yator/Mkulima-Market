package com.mkulimamarket.app

import android.app.Application
import android.content.Context

class AppContext : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = applicationContext
    }

    companion object {
        lateinit var instance: Context
            private set
    }
}