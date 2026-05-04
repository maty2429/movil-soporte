package com.example.soporte.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.soporte.features.auth.presentation.login.LoginScreen
import com.example.soporte.features.main.presentation.MainScreen
import com.example.soporte.features.profile.presentation.autoassigned.AutoAssignedTicketScreen
import com.example.soporte.features.tickets.presentation.detail.TicketDetailScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginRoute,
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(MainRoute) {
                        popUpTo(LoginRoute) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable<MainRoute> {
            MainScreen(
                onTicketClick = { ticketId ->
                    navController.navigate(TicketDetailRoute(ticketId))
                },
                onTransferTicketClick = { ticketId, transferId, isReceivedTransfer ->
                    navController.navigate(
                        TicketDetailRoute(
                            idTicket = ticketId,
                            idTraspaso = transferId,
                            isReceivedTransfer = isReceivedTransfer,
                        ),
                    )
                },
                onCreateAutoAssignedTicketClick = {
                    navController.navigate(AutoAssignedTicketRoute)
                },
                onLogout = {
                    navController.navigate(LoginRoute) {
                        popUpTo(MainRoute) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<AutoAssignedTicketRoute> {
            AutoAssignedTicketScreen(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }

        composable<TicketDetailRoute> {
            val route = it.toRoute<TicketDetailRoute>()
            TicketDetailScreen(
                ticketId = route.idTicket,
                transferId = route.idTraspaso,
                isReceivedTransfer = route.isReceivedTransfer,
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}
