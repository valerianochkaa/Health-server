package com.example.features.register;
import kotlinx.serialization.Serializable

// Модель данных для получения информации о регистрации пользователя
@Serializable
data class RegisterReceiveRemote(
        val email: String,
        val password: String,
)

// Модель данных для ответа на запрос регистрации пользователя
@Serializable
data class RegisterResponseRemote(
        val token: String
)
