package com.example.soporte.features.profile.di

import com.example.soporte.features.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    viewModel { ProfileViewModel(sessionManager = get()) }
}
