package com.example.soporte.features.transfers.data

import com.example.soporte.core.network.SoporteApiClient
import com.example.soporte.features.transfers.data.mapper.toDomain
import com.example.soporte.features.transfers.domain.model.TransferSummary
import com.example.soporte.features.transfers.domain.repository.TransfersRepository

class RemoteTransfersRepository(
    private val apiClient: SoporteApiClient,
) : TransfersRepository {

    override suspend fun getReceivedTransfers(technicianId: Int): Result<List<TransferSummary>> =
        runCatching {
            apiClient.getReceivedTransfers(technicianId).map { it.toDomain() }
        }

    override suspend fun getSentTransfers(technicianId: Int): Result<List<TransferSummary>> =
        runCatching {
            apiClient.getSentTransfers(technicianId).map { it.toDomain() }
        }
}
