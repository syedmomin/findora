package com.findora.app

import android.app.Application
import com.findora.app.di.AppContainer

class FindoraApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
