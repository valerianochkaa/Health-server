package com.example.database.temperatures

import com.example.database.users.Users
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Temperatures: Table("temperatures") {
    val temperatureId = integer("temperatureId").autoIncrement()
    val userIdTemperature= integer("userIdTemperature").references(Users.userId)
    val temperatureValue = double("temperatureValue")
    val recordDate = varchar("recordDate", 50)

    override val primaryKey = PrimaryKey(temperatureId, name = "PK_Temperature_ID")

    fun getAllTemperatures(): List<TemperaturesDTO> {
        return transaction {
            Temperatures.selectAll().mapNotNull { row ->
                TemperaturesDTO(
                    temperatureId = row[temperatureId],
                    userIdTemperature = row[userIdTemperature],
                    temperatureValue = row[temperatureValue],
                    recordDate = row[recordDate]
                )
            }
        }
    }

    fun getTemperatureById(temperatureId: Int): TemperaturesDTO? {
        return transaction {
            Temperatures.select { Temperatures.temperatureId eq temperatureId }
                .mapNotNull { row ->
                    TemperaturesDTO(
                        temperatureId = row[Temperatures.temperatureId],
                        userIdTemperature = row[userIdTemperature],
                        temperatureValue = row[temperatureValue],
                        recordDate = row[recordDate]
                    )
                }.singleOrNull()
        }
    }

    fun getAllTemperaturesByUser(userId: Int): List<TemperaturesDTO> {
        return transaction {
            Temperatures.select { userIdTemperature  eq userId }.mapNotNull { row ->
                TemperaturesDTO(
                    temperatureId = row[temperatureId],
                    userIdTemperature = row[userIdTemperature],
                    temperatureValue = row[temperatureValue],
                    recordDate = row[recordDate]
                )
            }
        }
    }
}