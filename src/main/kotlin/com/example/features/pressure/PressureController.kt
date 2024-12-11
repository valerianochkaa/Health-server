package com.example.features.pressure

import com.example.database.pressures.Pressures
import com.example.database.pressures.PressuresDTO
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

class PressureController(private val call: ApplicationCall) {
    suspend fun getAllPressures() {
        val pressures = Pressures.getAllPressures()
        call.respond(pressures)
    }

    suspend fun getAllPressuresByUser() {
        val userId = call.parameters["userId"]?.toIntOrNull()
        if (userId != null) {
            val pressures = Pressures.getAllPressuresByUser(userId)
            if (pressures.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "No pressures found for user ID: $userId")
            } else {
                call.respond(pressures)
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
        }
    }

    suspend fun getPressureById() {
        val pressureId = call.parameters["pressureId"]?.toIntOrNull()
        if (pressureId != null) {
            val pressure = Pressures.getPressureById(pressureId)
            if (pressure != null) {
                call.respond(pressure)
            } else {
                call.respond(HttpStatusCode.NotFound, "Pressure not found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid pressure ID")
        }
    }

    suspend fun insertPressureAndGetId() {
        val token = getToken() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Token is missing")
            return
        }
        val userId = getUserIdByToken(token) ?: run {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token or user not found")
            return
        }
        val pressuresDTO = call.receive<PressuresDTO>().copy(userIdPressure = userId)
        transaction {
            Pressures.insert {
                it[userIdPressure] = userId
                it[upperValue] = pressuresDTO.upperValue
                it[lowerValue] = pressuresDTO.lowerValue
                it[pulseValue] = pressuresDTO.pulseValue
                it[recordDate] = pressuresDTO.recordDate
            }
        }
        call.respond(HttpStatusCode.Created, pressuresDTO)
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

    suspend fun deletePressureById() {
        val pressureId = call.parameters["pressureId"]?.toIntOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest, "Invalid pressure ID")
        val exists = transaction {
            Pressures.select { Pressures.pressureId eq pressureId }.count() > 0
        }
        if (!exists) return call.respond(HttpStatusCode.NotFound, "Pressure record with ID $pressureId not found")
        transaction {
            Pressures.deleteWhere { Pressures.pressureId eq pressureId }
        }
        call.respond(HttpStatusCode.OK, "Pressure record with ID $pressureId has been deleted")
    }
}

