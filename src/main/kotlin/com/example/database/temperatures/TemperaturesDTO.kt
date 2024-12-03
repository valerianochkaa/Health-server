package com.example.database.temperatures

data class TemperaturesDTO (
    val temperatureId: Int? = null,
    val userIdTemperature: Int,
    val temperatureValue: Double,
    val recordDate: String
)