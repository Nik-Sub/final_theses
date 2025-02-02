package com.mobile.iwbi.android

import android.app.Application
import com.mobile.iwbi.appbase.di.KoinIntializer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application(){
    private val koinInitializer = KoinIntializer()
    override fun onCreate() {
        super.onCreate()

        koinInitializer.init {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}