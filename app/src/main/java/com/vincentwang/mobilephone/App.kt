package com.vincentwang.mobilephone

import android.app.Application
import com.vincentwang.mobilephone.di.currencyModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class App : Application(){

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Start Koin
        GlobalContext.startKoin {
            androidLogger()
            androidContext(this@App)
            modules(currencyModule)
        }
    }

}