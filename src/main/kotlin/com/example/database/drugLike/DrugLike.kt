package com.example.database.drugLike

import com.example.database.drugs.Drugs
import com.example.database.users.Users
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object DrugLike : Table("drug_like") {
    val drugIdLike = integer("drugIdLike").references(Drugs.drugId)
    val userIdDrugLike = integer("userIdDrugLike").references(Users.userId)

    fun getDrugLikesByUser(userId: Int): List<DrugLikeDTO> {
        return transaction {
            DrugLike.select { userIdDrugLike eq userId }.mapNotNull { row ->
                DrugLikeDTO(
                    drugIdLike = row[drugIdLike],
                    userIdDrugLike = row[userIdDrugLike]
                )
            }
        }
    }
}

