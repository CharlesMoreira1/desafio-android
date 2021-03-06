package com.desafioandroid

import android.app.Application
import com.desafioandroid.core.di.module.allModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null
        fun applicationContext() = instance as MyApplication
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(allModule)
        }
    }
}