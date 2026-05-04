package com.example.soporte.features.profile.presentation.autoassigned

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soporte.features.tickets.presentation.detail.components.SwipeToActionButton
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoAssignedTicketScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AutoAssignedTicketViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nuevo Ticket",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            AutoAssignedTicketForm(
                state = state,
                onRequesterRutChange = viewModel::onRequesterRutChange,
                onVerifyRequesterClick = viewModel::onVerifyRequesterClick,
                onServiceSelected = viewModel::onServiceSelected,
                onTicketTypeSelected = viewModel::onTicketTypeSelected,
                onPriorityLevelSelected = viewModel::onPriorityLevelSelected,
                onFailureCatalogSelected = viewModel::onFailureCatalogSelected,
                onCriticalChange = viewModel::onCriticalChange,
                onReportedFailureChange = viewModel::onReportedFailureChange,
                onLocationObservationChange = viewModel::onLocationObservationChange,
                onCreateTicketClick = viewModel::onCreateTicketClick,
                onRetryOptionsClick = viewModel::retryOptions,
            )
        }
    }
}

@Composable
private fun AutoAssignedTicketForm(
    state: AutoAssignedTicketState,
    onRequesterRutChange: (String) -> Unit,
    onVerifyRequesterClick: () -> Unit,
    onServiceSelected: (Int) -> Unit,
    onTicketTypeSelected: (Int) -> Unit,
    onPriorityLevelSelected: (Int) -> Unit,
    onFailureCatalogSelected: (Int) -> Unit,
    onCriticalChange: (Boolean) -> Unit,
    onReportedFailureChange: (String) -> Unit,
    onLocationObservationChange: (String) -> Unit,
    onCreateTicketClick: () -> Unit,
    onRetryOptionsClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Sección de Solicitante
        FormSection(title = "Información del Solicitante", icon = Icons.Default.Person) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.requesterRut,
                    onValueChange = onRequesterRutChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("RUT solicitante") },
                    placeholder = { Text("12345678-9") },
                    singleLine = true,
                    enabled = !state.isCheckingRequester && !state.isCreatingTicket,
                    shape = MaterialTheme.shapes.medium
                )
                FilledTonalIconButton(
                    onClick = onVerifyRequesterClick,
                    enabled = !state.isCheckingRequester && state.requesterRut.isNotBlank(),
                    modifier = Modifier.size(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (state.isCheckingRequester) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Search, contentDescription = "Verificar")
                    }
                }
            }

            AnimatedVisibility(visible = state.requester != null) {
                state.requester?.let { requester ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = requester.fullName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            state.requesterError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Sección de Detalles del Ticket
        FormSection(title = "Detalles del Servicio", icon = Icons.AutoMirrored.Filled.Assignment) {
            SearchableSelector(
                label = "Servicio",
                selectedText = state.services.firstOrNull { it.id == state.selectedServiceId }?.label,
                options = state.services,
                enabled = !state.isLoadingOptions && !state.isCreatingTicket,
                optionLabel = { it.label },
                onSelected = { onServiceSelected(it.id) }
            )

            SearchableSelector(
                label = "Catálogo de Falla",
                selectedText = state.failureCatalogs.firstOrNull { it.id == state.selectedFailureCatalogId }?.label,
                options = state.failureCatalogs,
                enabled = !state.isLoadingOptions && !state.isCreatingTicket,
                optionLabel = { it.label },
                onSelected = { onFailureCatalogSelected(it.id) }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    SimpleDropdownSelector(
                        label = "Tipo",
                        selectedText = state.ticketTypes.firstOrNull { it.id == state.selectedTicketTypeId }?.description,
                        options = state.ticketTypes,
                        enabled = !state.isLoadingOptions && !state.isCreatingTicket,
                        optionLabel = { it.description },
                        onSelected = { onTicketTypeSelected(it.id) }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    SimpleDropdownSelector(
                        label = "Prioridad",
                        selectedText = state.priorityLevels.firstOrNull { it.id == state.selectedPriorityLevelId }?.description,
                        options = state.priorityLevels,
                        enabled = !state.isLoadingOptions && !state.isCreatingTicket,
                        optionLabel = { it.description },
                        onSelected = { onPriorityLevelSelected(it.id) }
                    )
                }
            }

            Surface(
                color = if (state.isCritical) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().clickable { onCriticalChange(!state.isCritical) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = if (state.isCritical) Icons.Default.Warning else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (state.isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Ticket Crítico",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Switch(
                        checked = state.isCritical,
                        onCheckedChange = onCriticalChange,
                        enabled = !state.isCreatingTicket,
                    )
                }
            }
        }

        // Sección de Observaciones
        FormSection(title = "Descripción y Ubicación", icon = Icons.Default.Description) {
            OutlinedTextField(
                value = state.reportedFailure,
                onValueChange = onReportedFailureChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Falla reportada") },
                placeholder = { Text("Describe brevemente el problema...") },
                minLines = 3,
                enabled = !state.isCreatingTicket,
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = state.locationObservation,
                onValueChange = onLocationObservationChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Observación de ubicación") },
                placeholder = { Text("Ej: Segundo piso, oficina 204...") },
                minLines = 2,
                enabled = !state.isCreatingTicket,
                shape = MaterialTheme.shapes.medium
            )
        }

        state.submitError?.let { error ->
            Surface(
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        state.successMessage?.let { message ->
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        SwipeToActionButton(
            text = "DESLIZA PARA CREAR",
            onAction = onCreateTicketClick,
            isLoading = state.isCreatingTicket,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}

@Composable
private fun <T> SearchableSelector(
    label: String,
    selectedText: String?,
    options: List<T>,
    enabled: Boolean,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp)
        )
        OutlinedButton(
            onClick = { showDialog = true },
            enabled = enabled && options.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (selectedText != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            Text(
                text = selectedText ?: "Seleccionar $label",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }

    if (showDialog) {
        SearchDialog(
            title = "Buscar $label",
            options = options,
            optionLabel = optionLabel,
            onSelected = {
                onSelected(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun <T> SimpleDropdownSelector(
    label: String,
    selectedText: String?,
    options: List<T>,
    enabled: Boolean,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp)
        )
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled && options.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (selectedText != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = selectedText ?: "Elegir",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    maxLines = 1
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.45f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            expanded = false
                            onSelected(option)
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SearchDialog(
    title: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredOptions = remember(searchQuery, options) {
        options.filter { optionLabel(it).contains(searchQuery, ignoreCase = true) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(title, fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Escribe para filtrar...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                                }
                            }
                        },
                        singleLine = true,
                        shape = CircleShape
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredOptions) { option ->
                            Surface(
                                onClick = { onSelected(option) },
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Label, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Text(
                                        text = optionLabel(option),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                        if (filteredOptions.isEmpty()) {
                            item {
                                Text(
                                    text = "No se encontraron resultados",
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
