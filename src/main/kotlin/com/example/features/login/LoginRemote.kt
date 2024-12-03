package com.example.features.login

import kotlinx.serialization.Serializable

// Модель данных для получения информации о входе пользователя
@Serializable
data class LoginReceiveRemote(
    val email: String,
    val password: String
)

// Модель данных для ответа на запрос входа пользователя
@Serializable
data class LoginResponseRemote(
    val token: String
)