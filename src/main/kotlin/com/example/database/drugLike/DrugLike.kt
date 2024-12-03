package com.example.database.drugLike

import com.example.database.drugs.Drugs
import com.example.database.users.Users
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DrugLike : Table("drug_like") {
    val drugIdLike = integer("drugIdLike").references(Drugs.drugId)
    val userIdDrugLike = integer("userIdDrugLike").references(Users.userId)

    fun getAllDrugLikes(): List<DrugLikeDTO> {
        return transaction {
            DrugLike.selectAll().mapNotNull { row ->
                DrugLikeDTO(
                    drugIdLike = row[drugIdLike],
                    userIdDrugLike = row[userIdDrugLike]
                )
            }
        }
    }

    fun getDrugLikeById(drugId: Int, userId: Int): DrugLikeDTO? {
        return transaction {
            DrugLike.select {
                (DrugLike.drugIdLike eq drugId) and (DrugLike.userIdDrugLike eq userId)
            }.mapNotNull { row ->
                DrugLikeDTO(
                    drugIdLike = row[drugIdLike],
                    userIdDrugLike = row[userIdDrugLike]
                )
            }.singleOrNull()
        }
    }
}

