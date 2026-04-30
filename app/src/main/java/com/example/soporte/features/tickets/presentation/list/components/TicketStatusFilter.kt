package com.example.soporte.features.tickets.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.soporte.features.tickets.domain.model.TicketStatus
import com.example.soporte.features.tickets.domain.model.ticketFilterStatuses
import com.example.soporte.features.tickets.presentation.toColor

@Composable
fun TicketStatusFilter(
    selectedStatus: TicketStatus,
    onStatusClick: (TicketStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(ticketFilterStatuses) { status ->
            val isSelected = selectedStatus == status
            val statusColor = status.toColor()
            
            FilterChip(
                selected = isSelected,
                onClick = { onStatusClick(status) },
                label = { 
                    Text(
                        text = status.label,
                        style = MaterialTheme.typography.labelMedium
                    ) 
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = statusColor.copy(alpha = 0.2f),
                    selectedLabelColor = statusColor,
                    selectedLeadingIconColor = statusColor
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = statusColor
                )
            )
        }
    }
}
