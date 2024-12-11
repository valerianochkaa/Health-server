package com.example.features.temperature

import com.example.database.temperatures.TemperaturesDTO
import com.example.database.temperatures.Temperatures
import com.example.database.tokens.Tokens
import com.example.database.users.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class TemperatureController(private val call: ApplicationCall) {
    suspend fun getAllTemperatures() {
        val temperatures = Temperatures.getAllTemperatures()
        call.respond(temperatures)
    }

    suspend fun getAllTemperaturesByUser() {
        val userId = call.parameters["userId"]?.toIntOrNull()
        if (userId != null) {
            val temperatures = Temperatures.getAllTemperaturesByUser(userId)
            if (temperatures.isEmpty()) call.respond(HttpStatusCode.NotFound, "No temperatures found for user ID: $userId")
            else call.respond(temperatures)
        } else call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
    }

    suspend fun getTemperatureById() {
        val temperatureId = call.parameters["temperatureId"]?.toIntOrNull()
        if (temperatureId != null) {
            val temperature = Temperatures.getTemperatureById(temperatureId)
            if (temperature != null) call.respond(temperature)
            else call.respond(HttpStatusCode.NotFound, "Temperature not found")
        } else call.respond(HttpStatusCode.BadRequest, "Invalid temperature ID")
    }

    suspend fun insertTemperatureAndGetId() {
        val token = getToken() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Token is missing")
            return
        }
        val userId = getUserIdByToken(token) ?: run {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token or user not found")
            return
        }
        val temperaturesDTO = call.receive<TemperaturesDTO>().copy(userIdTemperature = userId)
        transaction {
            Temperatures.insert {
                it[userIdTemperature] = userId
                it[temperatureValue] = temperaturesDTO.temperatureValue
                it[recordDate] = temperaturesDTO.recordDate
            }
        }
        call.respond(HttpStatusCode.Created, temperaturesDTO)
    }

    private fun getToken(): String? {
        return call.request.headers["Authorization"]?.removePrefix("Bearer ")
    }

    private fun getUserIdByToken(token: String): Int? {
        val tokenRecord = transaction {
            Tokens.select { Tokens.token eq token }.singleOrNull()
        }
        return tokenRecord?.let {
            val userEmail = it[Tokens.tokenEmail]
            transaction {
                Users.select { Users.userEmail eq userEmail }.singleOrNull()?.get(Users.userId)
            }
        }
    }

    suspend fun deleteTemperatureById() {
        val temperatureId = call.parameters["temperatureId"]?.toIntOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest, "Invalid temperatureId")
        val exists = transaction {
            Temperatures.select { Temperatures.temperatureId eq temperatureId }.count() > 0
        }
        if (!exists) return call.respond(HttpStatusCode.NotFound, "Temperature record with ID $temperatureId not found")
        transaction {
            Temperatures.deleteWhere { Temperatures.temperatureId eq temperatureId }
        }
        call.respond(HttpStatusCode.OK, "Temperature record with ID $temperatureId has been deleted")
    }
}
