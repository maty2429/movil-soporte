package com.example.soporte.features.profile.domain.repository

import com.example.soporte.features.profile.domain.model.AutoAssignedRequester
import com.example.soporte.features.profile.domain.model.AutoAssignedTicketFormOptions
import com.example.soporte.features.profile.domain.model.CreateAutoAssignedTicketInput

interface AutoAssignedTicketRepository {
    suspend fun getFormOptions(): Result<AutoAssignedTicketFormOptions>
    suspend fun findRequesterByRut(rut: String): Result<AutoAssignedRequester>
    suspend fun createAutoAssignedTicket(input: CreateAutoAssignedTicketInput): Result<Unit>
}
