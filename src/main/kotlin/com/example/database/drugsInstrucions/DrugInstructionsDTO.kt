package com.example.database.drugsInstrucions

data class DrugInstructionsDTO (
    val drugInstructionId : Int? = null,
    val description: String,
    val activeIngredients: String,
    val composition: String,
    val indications: String,
    val contraindications: String,
    val sideEffects: String,
    val overdose: String,
    val storageConditions: String,
    val prescriptionRequirements: Boolean
)