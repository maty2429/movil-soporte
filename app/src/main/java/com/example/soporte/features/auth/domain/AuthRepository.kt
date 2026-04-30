package com.example.soporte.features.auth.domain

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
}
