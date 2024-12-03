package com.example.database.drugCategory

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DrugCategory: Table("drug_categories") {
    val drugCategoryId = integer("drugCategoryId").autoIncrement()
    val drugCategoryName = varchar("drugCategoryName", 50)

    override val primaryKey = PrimaryKey(drugCategoryId, name = "PK_Drug_Category_ID")

    fun getAllDrugCategories(): List<DrugCategoryDTO> {
        return transaction {
            DrugCategory.selectAll().mapNotNull { row ->
                DrugCategoryDTO(
                    drugCategoryId = row[drugCategoryId],
                    drugCategoryName = row[drugCategoryName]
                )
            }
        }
    }

    fun getDrugCategoryById(categoryId: Int): DrugCategoryDTO? {
        return transaction {
            DrugCategory.select { DrugCategory.drugCategoryId eq categoryId }
                .mapNotNull { row ->
                    DrugCategoryDTO(
                        drugCategoryId = row[drugCategoryId],
                        drugCategoryName = row[drugCategoryName]
                    )
                }.singleOrNull()
        }
    }
}
