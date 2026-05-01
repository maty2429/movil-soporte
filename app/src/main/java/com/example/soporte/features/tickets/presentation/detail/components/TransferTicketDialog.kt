package com.example.soporte.features.tickets.presentation.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.soporte.features.tickets.domain.model.Technician

@Composable
fun TransferTicketDialog(
    technicians: List<Technician>,
    isLoadingTechnicians: Boolean,
    selectedTechnicianId: Int?,
    reason: String,
    isCreatingTransfer: Boolean,
    error: String?,
    onTechnicianSelected: (Int) -> Unit,
    onReasonChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Traspasar ticket",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Selecciona el técnico destino e ingresa el motivo del traspaso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                when {
                    isLoadingTechnicians -> Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    }

                    technicians.isEmpty() -> Text(
                        text = "No hay técnicos disponibles para traspasar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    else -> LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp),
                    ) {
                        items(technicians) { technician ->
                            TechnicianOption(
                                technician = technician,
                                isSelected = technician.id == selectedTechnicianId,
                                isEnabled = !isCreatingTransfer,
                                onSelected = {
                                    technician.id?.let(onTechnicianSelected)
                                },
                            )
                            HorizontalDivider()
                        }
                    }
                }

                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Motivo") },
                    minLines = 3,
                    enabled = !isCreatingTransfer,
                    shape = MaterialTheme.shapes.medium,
                )

                if (error != null) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isCreatingTransfer &&
                    !isLoadingTechnicians &&
                    selectedTechnicianId != null &&
                    reason.isNotBlank(),
                shape = MaterialTheme.shapes.medium,
            ) {
                if (isCreatingTransfer) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Solicitar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isCreatingTransfer,
            ) {
                Text("Cancelar")
            }
        },
    )
}

@Composable
private fun TechnicianOption(
    technician: Technician,
    isSelected: Boolean,
    isEnabled: Boolean,
    onSelected: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled, onClick = onSelected),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
            enabled = isEnabled,
        )
        Text(
            text = technician.fullName ?: "Técnico sin nombre",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
