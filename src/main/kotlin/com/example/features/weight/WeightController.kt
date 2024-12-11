package com.example.features.weight

import com.example.database.tokens.Tokens
import com.example.database.users.Users
import com.example.database.weights.WeightsDTO
import com.example.database.weights.Weights
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class WeightController(private val call: ApplicationCall) {
    suspend fun insertWeightAndGetId() {
        val token = getToken() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Token is missing")
            return
        }
        val userId = getUserIdByToken(token) ?: run {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token or user not found")
            return
        }
        val weightsDTO = call.receive<WeightsDTO>().copy(userIdWeights = userId)
        transaction {
            Weights.insert {
                it[userIdWeights] = userId
                it[weightValue] = weightsDTO.weightValue
                it[recordDate] = weightsDTO.recordDate
            }
        }
        call.respond(HttpStatusCode.Created, weightsDTO)
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

    suspend fun getAllWeights() {
        val weights = Weights.getAllWeights()
        call.respond(weights)
    }

    suspend fun getAllWeightsByUser() {
        val userId = call.parameters["userId"]?.toIntOrNull()
        if (userId != null) {
            val weights = Weights.getAllWeightsByUser(userId)
            if (weights.isEmpty()) call.respond(HttpStatusCode.NotFound, "No weights found for user ID: $userId")
            else call.respond(weights)
        } else call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
    }

    suspend fun getWeightById() {
        val weightId = call.parameters["weightId"]?.toIntOrNull()
        if (weightId != null) {
            val weight = Weights.getWeightById(weightId)
            if (weight != null) call.respond(weight)
            else call.respond(HttpStatusCode.NotFound, "Weight not found")
        } else call.respond(HttpStatusCode.BadRequest, "Invalid weight ID")
    }

    suspend fun deleteWeightById() {
        val weightId = call.parameters["weightId"]?.toIntOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest, "Invalid weight ID")
        val exists = transaction {
            Weights.select { Weights.weightId eq weightId }.count() > 0
        }
        if (!exists) return call.respond(HttpStatusCode.NotFound, "Weight record with ID $weightId not found")
        transaction {
            Weights.deleteWhere { Weights.weightId eq weightId }
        }
        call.respond(HttpStatusCode.OK, "Weight record with ID $weightId has been deleted")
    }
}

