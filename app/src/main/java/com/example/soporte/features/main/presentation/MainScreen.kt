package com.example.soporte.features.main.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.soporte.features.inventory.presentation.InventoryScreen
import com.example.soporte.features.profile.presentation.ProfileScreen
import com.example.soporte.features.tickets.presentation.list.TicketsScreen
import com.example.soporte.features.transfers.presentation.TransfersScreen

private enum class MainTab(
    val label: String,
    val icon: ImageVector,
) {
    Tickets("Tickets", Icons.Default.ConfirmationNumber),
    Transfers("Traspasos", Icons.Default.SwapHoriz),
    Inventory("Inventario", Icons.Default.Inventory),
    Profile("Perfil", Icons.Default.Person),
}

@Composable
fun MainScreen(
    onTicketClick: (Int) -> Unit,
    onTransferTicketClick: (ticketId: Int, transferId: Int?, isReceivedTransfer: Boolean) -> Unit,
    onLogout: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(MainTab.Tickets) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = { 
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label 
                            ) 
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // No consumas insets aquí
    ) { innerPadding ->
        val screenModifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        
        when (selectedTab) {
            MainTab.Tickets -> TicketsScreen(
                modifier = screenModifier,
                onTicketClick = onTicketClick,
            )

            MainTab.Transfers -> TransfersScreen(
                modifier = screenModifier,
                onTransferClick = onTransferTicketClick,
            )

            MainTab.Inventory -> InventoryScreen(
                modifier = screenModifier,
            )

            MainTab.Profile -> ProfileScreen(
                modifier = screenModifier,
                onLogout = onLogout,
            )
        }
    }
}
