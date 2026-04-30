package com.example.soporte.features.tickets.domain.usecase

import com.example.soporte.features.tickets.domain.repository.TicketsRepository

class GetTechnicianByRutUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(rut: String) =
        repository.getTechnicianByRut(rut)
}
