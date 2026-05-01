package com.example.soporte.features.auth.presentation.login

data class LoginState(
    val rut: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
)
