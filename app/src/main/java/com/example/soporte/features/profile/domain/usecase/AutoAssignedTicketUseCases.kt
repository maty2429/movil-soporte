package com.example.soporte.features.profile.domain.usecase

import com.example.soporte.features.profile.domain.model.CreateAutoAssignedTicketInput
import com.example.soporte.features.profile.domain.repository.AutoAssignedTicketRepository

class GetAutoAssignedTicketFormOptionsUseCase(
    private val repository: AutoAssignedTicketRepository,
) {
    suspend operator fun invoke() =
        repository.getFormOptions()
}

class FindAutoAssignedRequesterUseCase(
    private val repository: AutoAssignedTicketRepository,
) {
    suspend operator fun invoke(rut: String) =
        repository.findRequesterByRut(rut)
}

class CreateAutoAssignedTicketUseCase(
    private val repository: AutoAssignedTicketRepository,
) {
    suspend operator fun invoke(input: CreateAutoAssignedTicketInput) =
        repository.createAutoAssignedTicket(input)
}
