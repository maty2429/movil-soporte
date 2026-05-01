package com.example.soporte.features.transfers.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soporte.features.tickets.presentation.toDisplayDateTime
import com.example.soporte.features.transfers.domain.model.TransferSummary
import com.example.soporte.features.transfers.domain.model.TransferSummaryType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransfersScreen(
    modifier: Modifier = Modifier,
    onTransferClick: (ticketId: Int, transferId: Int?, isReceivedTransfer: Boolean) -> Unit,
    viewModel: TransfersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Traspasos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        state.technicianName?.let { name ->
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        TransfersContent(
            state = state,
            modifier = Modifier.padding(padding),
            onTabClick = viewModel::selectTab,
            onRetryClick = viewModel::refresh,
            onTransferClick = { transfer ->
                val isReceivedTransfer = transfer.type == TransferSummaryType.Received
                onTransferClick(
                    transfer.ticketId,
                    if (isReceivedTransfer) transfer.id else null,
                    isReceivedTransfer,
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransfersContent(
    state: TransfersState,
    modifier: Modifier,
    onTabClick: (TransfersTab) -> Unit,
    onRetryClick: () -> Unit,
    onTransferClick: (TransferSummary) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val tabs = listOf(
            TransfersTab.Received to "Recibidos",
            TransfersTab.Sent to "Solicitados",
        )
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabs.forEachIndexed { index, (tab, label) ->
                SegmentedButton(
                    selected = state.selectedTab == tab,
                    onClick = { onTabClick(tab) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                ) {
                    Text(label)
                }
            }
        }

        when {
            state.isLoading -> LoadingTransfers()
            state.error != null -> ErrorTransfers(
                message = state.error,
                onRetryClick = onRetryClick,
            )
            state.transfers.isEmpty() -> EmptyTransfers()
            else -> TransferList(
                transfers = state.transfers,
                onTransferClick = onTransferClick,
            )
        }
    }
}

@Composable
private fun LoadingTransfers() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorTransfers(
    message: String,
    onRetryClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onRetryClick) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun EmptyTransfers() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Sin traspasos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp),
        )
    }
}

@Composable
private fun TransferList(
    transfers: List<TransferSummary>,
    onTransferClick: (TransferSummary) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        items(
            items = transfers,
            key = { it.id },
        ) { transfer ->
            TransferSummaryCard(
                transfer = transfer,
                onClick = { onTransferClick(transfer) },
            )
        }
    }
}

@Composable
private fun TransferSummaryCard(
    transfer: TransferSummary,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = transfer.ticketNumber.ifBlank { "Ticket #${transfer.ticketId}" },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = transfer.statusDescription.ifBlank { transfer.statusCode },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = transfer.reason.ifBlank { "Sin motivo informado" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TransferInfoLine("Origen", transfer.originTechnicianName)
                TransferInfoLine("Destino", transfer.destinationTechnicianName)
                transfer.requestedAt?.takeIf { it.isNotBlank() }?.let { date ->
                    TransferInfoLine("Fecha", date.toDisplayDateTime())
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
private fun TransferInfoLine(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value.ifBlank { "No informado" },
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
