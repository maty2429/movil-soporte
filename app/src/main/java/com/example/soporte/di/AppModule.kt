package com.example.soporte.di

import com.example.soporte.core.session.SessionManager
import org.koin.dsl.module

val appModule = module {
    single { SessionManager() }
}
