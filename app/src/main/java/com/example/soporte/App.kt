package com.example.soporte

import android.app.Application
import com.example.soporte.core.network.networkModule
import com.example.soporte.core.storage.storageModule
import com.example.soporte.di.appModule
import com.example.soporte.features.auth.di.authModule
import com.example.soporte.features.profile.di.profileModule
import com.example.soporte.features.tickets.di.ticketsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                appModule,
                networkModule,
                storageModule,
                authModule,
                profileModule,
                ticketsModule,
            )
        }
    }
}
