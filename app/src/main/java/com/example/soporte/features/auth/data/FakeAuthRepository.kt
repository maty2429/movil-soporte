package com.example.soporte.features.auth.data

import com.example.soporte.features.auth.domain.AuthRepository
import kotlinx.coroutines.delay

class FakeAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(350)

        return when {
            email.isBlank() -> Result.failure(IllegalArgumentException("Ingresa tu email"))
            password.isBlank() -> Result.failure(IllegalArgumentException("Ingresa tu contrasena"))
            else -> Result.success(Unit)
        }
    }
}
