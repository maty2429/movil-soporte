package com.example.soporte.features.transfers.di

import com.example.soporte.features.transfers.data.RemoteTransfersRepository
import com.example.soporte.features.transfers.domain.repository.TransfersRepository
import com.example.soporte.features.transfers.domain.usecase.GetReceivedTransfersUseCase
import com.example.soporte.features.transfers.domain.usecase.GetSentTransfersUseCase
import com.example.soporte.features.transfers.presentation.TransfersViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val transfersModule = module {
    single<TransfersRepository> { RemoteTransfersRepository(apiClient = get()) }
    factory { GetReceivedTransfersUseCase(repository = get()) }
    factory { GetSentTransfersUseCase(repository = get()) }
    viewModel {
        TransfersViewModel(
            getReceivedTransfers = get(),
            getSentTransfers = get(),
            sessionManager = get(),
        )
    }
}
