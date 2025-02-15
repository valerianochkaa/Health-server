package com.example.database.drugs

import com.example.database.drugCategory.DrugCategories
import com.example.database.drugCategory.DrugCategoriesDTO
import com.example.database.drugsInstrucions.DrugInstructions
import com.example.database.drugsInstrucions.DrugInstructionsDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Drugs : Table("drugs") {
    val drugId = integer("drugId").autoIncrement()
    val drugCategoryId = integer("drugCategoryId").references(DrugCategories.drugCategoryId)
    val drugInstructionId = integer("drugInstructionId").references(DrugInstructions.drugInstructionId)
    val drugName = varchar("drugName", 50)
    val drugPrice = double("drugPrice")
    val drugAnalog = varchar("drugAnalog", 50)

    override val primaryKey = PrimaryKey(drugId, name = "PK_Drug_ID")

    fun getDrugById(drugId: Int): DrugsDTO? {
        return transaction {
            Drugs.select { Drugs.drugId eq drugId }
                .mapNotNull { row ->
                    DrugsDTO(
                        drugId = row[Drugs.drugId],
                        drugCategoryId = row[drugCategoryId],
                        drugInstructionId = row[drugInstructionId],
                        drugName = row[drugName],
                        drugPrice = row[drugPrice],
                        drugAnalog = row[drugAnalog]
                    )
                }.singleOrNull()
        }
    }

    fun getDrugInstructionsByDrugId(drugId: Int): DrugInstructionsDTO? {
        return transaction {
            Drugs.select { Drugs.drugId eq drugId }
                .mapNotNull { row ->
                    val instructionId = row[drugInstructionId]
                    DrugInstructions.getDrugInstructionById(instructionId)
                }.singleOrNull()
        }
    }

    fun getAllDrugs(): List<DrugsDTO> {
        return transaction {
            Drugs.selectAll().mapNotNull { row ->
                DrugsDTO(
                    drugId = row[drugId],
                    drugCategoryId = row[drugCategoryId],
                    drugInstructionId = row[drugInstructionId],
                    drugName = row[drugName],
                    drugPrice = row[drugPrice],
                    drugAnalog = row[drugAnalog]
                )
            }
        }
    }


    fun getDrugsByCategory(categoryId: Int): List<DrugsDTO> {
        return transaction {
            Drugs.select { Drugs.drugCategoryId eq categoryId }
                .mapNotNull { row ->
                    DrugsDTO(
                        drugId = row[drugId],
                        drugCategoryId = row[drugCategoryId],
                        drugInstructionId = row[drugInstructionId],
                        drugName = row[drugName],
                        drugPrice = row[drugPrice],
                        drugAnalog = row[drugAnalog]
                    )
                }
        }
    }

    fun getCategoryByDrugId(drugId: Int): DrugCategoriesDTO? {
        return transaction {
            val categoryId = Drugs.select { Drugs.drugId eq drugId }
                .mapNotNull { row -> row[Drugs.drugCategoryId] }
                .singleOrNull()
            categoryId?.let {
                DrugCategories.getDrugCategoryById(it)
            }
        }
    }
}

