package com.example.database.drugsInstrucions

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DrugInstructions: Table("drug_instructions") {
    val drugInstructionId  = integer("drugInstructionId").autoIncrement()
    val description = varchar("description", 500)
    val activeIngredients = varchar("activeIngredients", 500)
    val composition = varchar("composition", 2500)
    val indications = varchar("indications", 2500)
    val contraindications = varchar("contraindications", 2500)
    val sideEffects = varchar("sideEffects", 2500)
    val overdose = varchar("overdose", 2500)
    val storageConditions = varchar("storageConditions", 500)
    val prescriptionRequirements = bool("prescriptionRequirements")

    override val primaryKey = PrimaryKey(drugInstructionId, name = "PK_Drug_Instruction_ID")

    fun getAllDrugInstructions(): List<DrugInstructionsDTO> {
        return transaction {
            DrugInstructions.selectAll().mapNotNull { row ->
                DrugInstructionsDTO(
                    drugInstructionId = row[drugInstructionId],
                    description = row[description],
                    activeIngredients = row[activeIngredients],
                    composition = row[composition],
                    indications = row[indications],
                    contraindications = row[contraindications],
                    sideEffects = row[sideEffects],
                    overdose = row[overdose],
                    storageConditions = row[storageConditions],
                    prescriptionRequirements = row[prescriptionRequirements]
                )
            }
        }
    }

    fun getDrugInstructionById(drugInstructionId: Int): DrugInstructionsDTO? {
        return transaction {
            DrugInstructions.select { DrugInstructions.drugInstructionId eq drugInstructionId }
                .mapNotNull { row ->
                    DrugInstructionsDTO(
                        drugInstructionId = row[DrugInstructions.drugInstructionId],
                        description = row[description],
                        activeIngredients = row[activeIngredients],
                        composition = row[composition],
                        indications = row[indications],
                        contraindications = row[contraindications],
                        sideEffects = row[sideEffects],
                        overdose = row[overdose],
                        storageConditions = row[storageConditions],
                        prescriptionRequirements = row[prescriptionRequirements]
                    )
                }.singleOrNull()
        }
    }
}

