package com.example.database.users

data class UserDTO (
    val userId: Int? = null,
    val userEmail: String,
    val userPassword: String
)