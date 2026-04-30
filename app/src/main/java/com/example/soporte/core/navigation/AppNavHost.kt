package com.example.soporte.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.soporte.features.auth.presentation.login.LoginScreen
import com.example.soporte.features.main.presentation.MainScreen
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
                onLogout = {
                    navController.navigate(LoginRoute) {
                        popUpTo(MainRoute) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<TicketDetailRoute> {
            val ticketId = it.toRoute<TicketDetailRoute>().idTicket
            TicketDetailScreen(
                ticketId = ticketId,
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}
