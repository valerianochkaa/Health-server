package com.example.features.token

import kotlinx.serialization.Serializable

@Serializable
data class TokenRemote(
    val valid: Boolean,
    val message: String
)