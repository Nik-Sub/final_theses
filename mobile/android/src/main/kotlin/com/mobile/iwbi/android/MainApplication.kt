package com.mobile.iwbi.android

import android.app.Application
import com.mobile.iwbi.appbase.di.KoinIntializer
import com.mobile.iwbi.infrastructure.InfrastructureConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application(){
    private val infrastructureConfig = InfrastructureConfig("http://10.0.2.2:8080/")
    private val koinInitializer = KoinIntializer(infrastructureConfig)
    override fun onCreate() {
        super.onCreate()

        koinInitializer.init {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}