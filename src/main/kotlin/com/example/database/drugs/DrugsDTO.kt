package com.example.database.drugs

data class DrugsDTO(
    val drugId: Int? = null,
    val drugCategoryId: Int,
    val drugInstructionId: Int,
    val drugName: String,
    val drugPrice: Double,
    val drugAnalog: String,
)