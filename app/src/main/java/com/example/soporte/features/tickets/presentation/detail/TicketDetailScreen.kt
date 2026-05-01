package com.example.soporte.features.tickets.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soporte.features.tickets.presentation.detail.components.CreateMilestoneDialog
import com.example.soporte.features.tickets.presentation.detail.components.HistoryDialog
import com.example.soporte.features.tickets.presentation.detail.components.PauseTicketDialog
import com.example.soporte.features.tickets.presentation.detail.components.TicketDetailBody
import com.example.soporte.features.tickets.presentation.detail.components.TransferTicketDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun TicketDetailScreen(
    ticketId: Int,
    transferId: Int? = null,
    isReceivedTransfer: Boolean = false,
    onBackClick: () -> Unit,
    viewModel: TicketDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(ticketId, transferId, isReceivedTransfer) {
        viewModel.loadTicket(
            ticketId = ticketId,
            transferId = transferId,
            isReceivedTransfer = isReceivedTransfer,
        )
    }

    TicketDetailContent(
        state = state,
        onStartTicketClick = viewModel::onStartTicketClick,
        onBackClick = onBackClick,
        onMilestoneClick = viewModel::onMilestoneClick,
        onMilestonesClick = viewModel::onMilestonesClick,
        onTransferClick = viewModel::onTransferClick,
        onPauseClick = viewModel::onPauseClick,
        onFinishPauseClick = viewModel::onFinishPauseClick,
        onAcceptTransferClick = { viewModel.onAcceptTransferClick(onBackClick) },
        onRejectTransferClick = { viewModel.onRejectTransferClick(onBackClick) },
    )

    if (state.isMilestoneDialogVisible) {
        CreateMilestoneDialog(
            observation = state.milestoneObservation,
            isCreating = state.isCreatingMilestone,
            error = state.milestoneError,
            onObservationChange = viewModel::onMilestoneObservationChange,
            onConfirm = viewModel::onCreateMilestoneClick,
            onDismiss = viewModel::onDismissMilestoneDialog,
        )
    }

    if (state.isMilestonesDialogVisible) {
        HistoryDialog(
            milestones = state.milestones,
            isLoading = state.isLoadingMilestones,
            error = state.milestonesError,
            onDismiss = viewModel::onDismissMilestonesDialog,
        )
    }

    if (state.isTransferDialogVisible) {
        TransferTicketDialog(
            technicians = state.transferTechnicians,
            isLoadingTechnicians = state.isLoadingTransferTechnicians,
            selectedTechnicianId = state.selectedTransferTechnicianId,
            reason = state.transferReason,
            isCreatingTransfer = state.isCreatingTransfer,
            error = state.transferError,
            onTechnicianSelected = viewModel::onTransferTechnicianSelected,
            onReasonChange = viewModel::onTransferReasonChange,
            onConfirm = viewModel::onCreateTransferClick,
            onDismiss = viewModel::onDismissTransferDialog,
        )
    }

    if (state.isPauseDialogVisible) {
        PauseTicketDialog(
            reasons = state.pauseReasons,
            isLoadingReasons = state.isLoadingPauseReasons,
            selectedReasonId = state.selectedPauseReasonId,
            isCreatingPause = state.isCreatingPause,
            error = state.pauseError,
            onReasonSelected = viewModel::onPauseReasonSelected,
            onConfirm = viewModel::onCreatePauseClick,
            onDismiss = viewModel::onDismissPauseDialog,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketDetailContent(
    state: TicketDetailState,
    onStartTicketClick: () -> Unit,
    onBackClick: () -> Unit,
    onMilestoneClick: () -> Unit,
    onMilestonesClick: () -> Unit,
    onTransferClick: () -> Unit,
    onPauseClick: () -> Unit,
    onFinishPauseClick: () -> Unit,
    onAcceptTransferClick: () -> Unit,
    onRejectTransferClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de Ticket",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )

                state.error != null -> Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )

                state.ticket != null -> TicketDetailBody(
                    ticket = state.ticket,
                    isStartingTicket = state.isStartingTicket,
                    isFinishingPause = state.isFinishingPause,
                    finishPauseError = state.finishPauseError,
                    onStartTicketClick = onStartTicketClick,
                    onMilestoneClick = onMilestoneClick,
                    onMilestonesClick = onMilestonesClick,
                    onTransferClick = onTransferClick,
                    onPauseClick = onPauseClick,
                    onFinishPauseClick = onFinishPauseClick,
                    canRespondToTransfer = state.canRespondToTransfer,
                    isRespondingTransfer = state.isRespondingTransfer,
                    respondingTransferAction = state.respondingTransferAction,
                    transferResponseError = state.transferResponseError,
                    onAcceptTransferClick = onAcceptTransferClick,
                    onRejectTransferClick = onRejectTransferClick,
                )
            }
        }
    }
}
