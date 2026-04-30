package com.example.soporte.core.session

import com.example.soporte.features.tickets.domain.model.Technician
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager {
    private val _technician = MutableStateFlow<Technician?>(null)
    val technician = _technician.asStateFlow()

    fun setTechnician(technician: Technician) {
        _technician.value = technician
    }

    fun clearSession() {
        _technician.value = null
    }
}
