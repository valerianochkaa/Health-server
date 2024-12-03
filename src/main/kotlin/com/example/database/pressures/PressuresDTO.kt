package com.example.database.pressures

data class PressuresDTO(
    val pressureId: Int? = null,
    val userIdPressure: Int,
    val upperValue: Int,
    val lowerValue: Int,
    val pulseValue: Int,
    val recordDate: String
)