package com.example.soporte.features.transfers.presentation

import com.example.soporte.features.transfers.domain.model.TransferSummary

data class TransfersState(
    val selectedTab: TransfersTab = TransfersTab.Received,
    val technicianName: String? = null,
    val transfers: List<TransferSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

enum class TransfersTab {
    Received,
    Sent,
}
