package com.example.database.weights

import com.example.database.users.Users
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Weights: Table("weights") {
    val weightId = integer("weightId").autoIncrement()
    val userIdWeights = integer("userIdWeights").references(Users.userId)
    val weightValue = double("weightValue")
    val recordDate = varchar("recordDate", 50)

    override val primaryKey = PrimaryKey(weightId, name = "PK_Weight_ID")

    fun getAllWeights(): List<WeightsDTO> {
        return transaction {
            Weights.selectAll().mapNotNull { row ->
                WeightsDTO(
                    weightId = row[weightId],
                    userIdWeights = row[userIdWeights],
                    weightValue = row[weightValue],
                    recordDate = row[recordDate]
                )
            }
        }
    }

    fun getWeightById(weightId: Int): WeightsDTO? {
        return transaction {
            Weights.select { Weights.weightId eq weightId }
                .mapNotNull { row ->
                    WeightsDTO(
                        weightId = row[Weights.weightId],
                        userIdWeights = row[userIdWeights],
                        weightValue = row[weightValue],
                        recordDate = row[recordDate]
                    )
                }.singleOrNull()
        }
    }

    fun getAllWeightsByUser(userId: Int): List<WeightsDTO> {
        return transaction {
            Weights.select { userIdWeights eq userId }.mapNotNull { row ->
                WeightsDTO(
                    weightId = row[weightId],
                    userIdWeights = row[userIdWeights],
                    weightValue = row[weightValue],
                    recordDate = row[recordDate]
                )
            }
        }
    }
}