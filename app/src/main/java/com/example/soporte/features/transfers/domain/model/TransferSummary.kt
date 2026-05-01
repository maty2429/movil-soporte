package com.example.soporte.features.transfers.domain.model

data class TransferSummary(
    val id: Int,
    val ticketId: Int,
    val ticketNumber: String,
    val reason: String,
    val requestedAt: String?,
    val originTechnicianName: String,
    val destinationTechnicianName: String,
    val statusCode: String,
    val statusDescription: String,
    val type: TransferSummaryType,
)

enum class TransferSummaryType {
    Received,
    Sent,
}
