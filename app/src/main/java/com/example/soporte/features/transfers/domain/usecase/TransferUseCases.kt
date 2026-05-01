package com.example.soporte.features.transfers.domain.usecase

import com.example.soporte.features.transfers.domain.repository.TransfersRepository

class GetReceivedTransfersUseCase(
    private val repository: TransfersRepository,
) {
    suspend operator fun invoke(technicianId: Int) =
        repository.getReceivedTransfers(technicianId)
}

class GetSentTransfersUseCase(
    private val repository: TransfersRepository,
) {
    suspend operator fun invoke(technicianId: Int) =
        repository.getSentTransfers(technicianId)
}
