package com.example.soporte.features.profile.presentation

import androidx.lifecycle.ViewModel
import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.tickets.domain.model.Technician
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(
    private val sessionManager: SessionManager,
) : ViewModel() {
    val technician: StateFlow<Technician?> = sessionManager.technician

    fun logout() {
        sessionManager.clearSession()
    }
}
