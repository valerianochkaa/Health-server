package com.example.database.pressures

import com.example.database.users.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Pressures : Table("pressures") {
    val pressureId = integer("pressureId").autoIncrement()
    val userIdPressure = integer("userIdPressure").references(Users.userId)
    val upperValue = integer("upperValue")
    val lowerValue = integer("lowerValue")
    val pulseValue = integer("pulseValue")
    val recordDate = varchar("recordDate", 50)

    override val primaryKey = PrimaryKey(pressureId, name = "PK_Pressure_ID")

    fun getAllPressures(): List<PressuresDTO> {
        return transaction {
            Pressures.selectAll().mapNotNull { row ->
                PressuresDTO(
                    pressureId = row[pressureId],
                    userIdPressure = row[userIdPressure],
                    upperValue = row[upperValue],
                    lowerValue = row[lowerValue],
                    pulseValue = row[pulseValue],
                    recordDate = row[recordDate]
                )
            }
        }
    }

    fun getPressureById(pressureId: Int): PressuresDTO? {
        return transaction {
            Pressures.select { Pressures.pressureId eq pressureId }
                .mapNotNull { row ->
                    PressuresDTO(
                        pressureId = row[Pressures.pressureId],
                        userIdPressure = row[userIdPressure],
                        upperValue = row[upperValue],
                        lowerValue = row[lowerValue],
                        pulseValue = row[pulseValue],
                        recordDate = row[recordDate]
                    )
                }.singleOrNull()
        }
    }

    fun getAllPressuresByUser(userId: Int): List<PressuresDTO> {
        return transaction {
            Pressures.select { userIdPressure eq userId }.mapNotNull { row ->
                PressuresDTO(
                    pressureId = row[pressureId],
                    userIdPressure = row[userIdPressure],
                    upperValue = row[upperValue],
                    lowerValue = row[lowerValue],
                    pulseValue = row[pulseValue],
                    recordDate = row[recordDate]
                )
            }
        }
    }
}
