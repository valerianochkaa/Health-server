package com.example.database.drugsInstrucions

// DrugsInstructionDTO
data class DrugInstructionsDTO (
    val drugInstructionId : Int? = null,
    val description: String? = null,
    val activeIngredients: String? = null,
    val composition: String? = null,
    val indications: String? = null,
    val contraindications: String? = null,
    val sideEffects: String? = null,
    val overdose: String? = null,
    val storageConditions: String? = null,
    val prescriptionRequirements: Boolean? = null
)