package com.oakraw.thailand_covid

import android.app.Application
import com.oakraw.thailand_covid.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.core.context.startKoin

class AppController : Application() {
    override fun onCreate() {
        super.onCreate()
        initiateKoin()
    }

    private fun initiateKoin() {
        startKoin{
            androidContext(this@AppController)
            modules(provideDependency())
            androidFileProperties()
        }
    }

    open fun provideDependency() = appComponent
}