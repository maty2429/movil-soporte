package com.example.soporte.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.tickets.domain.usecase.GetTechnicianByRutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val getTechnicianByRut: GetTechnicianByRutUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onRutChange(rut: String) {
        _state.update { it.copy(rut = rut, error = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun login() {
        val currentState = state.value
        val rut = currentState.rut.trim()
        if (rut.isBlank()) {
            _state.update { it.copy(error = "Ingresa el RUT del tecnico") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getTechnicianByRut(rut)
                .onSuccess { technician ->
                    sessionManager.setTechnician(technician)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "No se pudo obtener el tecnico",
                        )
                    }
                }
        }
    }
}
