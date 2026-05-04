package com.example.soporte.features.profile.di

import com.example.soporte.features.profile.data.RemoteAutoAssignedTicketRepository
import com.example.soporte.features.profile.domain.repository.AutoAssignedTicketRepository
import com.example.soporte.features.profile.domain.usecase.CreateAutoAssignedTicketUseCase
import com.example.soporte.features.profile.domain.usecase.FindAutoAssignedRequesterUseCase
import com.example.soporte.features.profile.domain.usecase.GetAutoAssignedTicketFormOptionsUseCase
import com.example.soporte.features.profile.presentation.autoassigned.AutoAssignedTicketViewModel
import com.example.soporte.features.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    single<AutoAssignedTicketRepository> { RemoteAutoAssignedTicketRepository(apiClient = get()) }
    factory { GetAutoAssignedTicketFormOptionsUseCase(repository = get()) }
    factory { FindAutoAssignedRequesterUseCase(repository = get()) }
    factory { CreateAutoAssignedTicketUseCase(repository = get()) }
    viewModel { ProfileViewModel(sessionManager = get()) }
    viewModel {
        AutoAssignedTicketViewModel(
            getFormOptions = get(),
            findRequester = get(),
            createTicket = get(),
            sessionManager = get(),
        )
    }
}
