package com.example.soporte.features.tickets.di

import com.example.soporte.features.tickets.data.RemoteTicketsRepository
import com.example.soporte.features.tickets.domain.repository.TicketsRepository
import com.example.soporte.features.tickets.domain.usecase.CreateTicketMilestoneUseCase
import com.example.soporte.features.tickets.domain.usecase.FinishTicketUseCase
import com.example.soporte.features.tickets.domain.usecase.FinishPauseUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTechnicianByRutUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTicketDetailUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTicketMilestonesUseCase
import com.example.soporte.features.tickets.domain.usecase.GetPauseReasonsUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTicketsByStatusUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTransferTechniciansUseCase
import com.example.soporte.features.tickets.domain.usecase.MarkTicketAsSeenUseCase
import com.example.soporte.features.tickets.domain.usecase.PauseTicketUseCase
import com.example.soporte.features.tickets.domain.usecase.RespondTransferUseCase
import com.example.soporte.features.tickets.domain.usecase.StartTicketUseCase
import com.example.soporte.features.tickets.domain.usecase.TransferTicketUseCase
import com.example.soporte.features.tickets.presentation.detail.TicketDetailViewModel
import com.example.soporte.features.tickets.presentation.list.TicketsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ticketsModule = module {
    single<TicketsRepository> { RemoteTicketsRepository(apiClient = get()) }
    factory { GetTechnicianByRutUseCase(repository = get()) }
    factory { GetTicketsByStatusUseCase(repository = get()) }
    factory { GetTicketDetailUseCase(repository = get()) }
    factory { GetTicketMilestonesUseCase(repository = get()) }
    factory { GetPauseReasonsUseCase(repository = get()) }
    factory { GetTransferTechniciansUseCase(repository = get()) }
    factory { MarkTicketAsSeenUseCase(repository = get()) }
    factory { StartTicketUseCase(repository = get()) }
    factory { CreateTicketMilestoneUseCase(repository = get()) }
    factory { PauseTicketUseCase(repository = get()) }
    factory { FinishPauseUseCase(repository = get()) }
    factory { FinishTicketUseCase() }
    factory { TransferTicketUseCase(repository = get()) }
    factory { RespondTransferUseCase(repository = get()) }
    viewModel {
        TicketsViewModel(
            repository = get(),
            getTechnicianByRut = get(),
            getTicketsByStatus = get(),
            markTicketAsSeen = get(),
            sessionManager = get(),
        )
    }
    viewModel { 
        TicketDetailViewModel(
            getTicketDetail = get(),
            getTicketMilestones = get(),
            getTransferTechnicians = get(),
            getPauseReasons = get(),
            startTicket = get(),
            createTicketMilestone = get(),
            transferTicket = get(),
            respondTransfer = get(),
            pauseTicket = get(),
            finishPause = get(),
            repository = get(),
            sessionManager = get(),
        ) 
    }
}
