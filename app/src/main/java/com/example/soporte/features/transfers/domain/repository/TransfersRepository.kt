package com.example.soporte.features.transfers.domain.repository

import com.example.soporte.features.transfers.domain.model.TransferSummary

interface TransfersRepository {
    suspend fun getReceivedTransfers(technicianId: Int): Result<List<TransferSummary>>
    suspend fun getSentTransfers(technicianId: Int): Result<List<TransferSummary>>
}
