package com.mobile.iwbi.android

import android.app.Application
import android.content.Context
import com.mobile.iwbi.appbase.di.KoinIntializer
import com.mobile.iwbi.infrastructure.InfrastructureConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class MainApplication : Application(){
    private val infrastructureConfig = InfrastructureConfig("http://10.0.2.2:8080/")
    private val koinInitializer = KoinIntializer(infrastructureConfig) {
            single<Context> { this@MainApplication }
    }

    override fun onCreate() {
        super.onCreate()

        koinInitializer.init {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}

