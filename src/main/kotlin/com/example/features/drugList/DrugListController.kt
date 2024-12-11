package com.example.features.drugList

import com.example.database.drugCategory.DrugCategories
import com.example.database.drugLike.DrugLike
import com.example.database.drugs.Drugs
import com.example.database.drugsInstrucions.DrugInstructions
import com.example.database.tokens.Tokens
import com.example.database.users.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class DrugListController(private val call: ApplicationCall) {
    suspend fun insertLike() {
        val token = getToken() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Token is missing")
            return
        }
        val userId = getUserIdByToken(token) ?: run {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token or user not found")
            return
        }
        val drugId = call.parameters["drugId"]?.toIntOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest, "Invalid drugId")
        val drugExists = transaction {
            Drugs.select { Drugs.drugId eq drugId }.count() > 0
        }
        if (!drugExists) return call.respond(HttpStatusCode.NotFound, "Drug with ID $drugId not found")
        transaction {
            DrugLike.insert {
                it[drugIdLike] = drugId
                it[userIdDrugLike] = userId
            }
        }
        call.respond(HttpStatusCode.Created, "Like added successfully for drug ID $drugId")
    }

    suspend fun deleteLikeByDrugId() {
        val token = getToken() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Token is missing")
            return
        }
        val userId = getUserIdByToken(token) ?: run {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token or user not found")
            return
        }
        val drugId = call.parameters["drugId"]?.toIntOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest, "Invalid drugId")
        val likeExists = transaction {
            DrugLike.select {
                (DrugLike.drugIdLike eq drugId) and (DrugLike.userIdDrugLike eq userId)
            }.count() > 0
        }
        if (!likeExists) return call.respond(HttpStatusCode.NotFound, "Like for drug ID $drugId not found for user ID $userId")
        transaction {
            DrugLike.deleteWhere {
                (DrugLike.drugIdLike eq drugId) and (DrugLike.userIdDrugLike eq userId)
            }
        }

        call.respond(HttpStatusCode.OK, "Like for drug ID $drugId has been deleted")
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

    suspend fun getAllDrugLikes() {
        val userId = call.parameters["userId"]?.toIntOrNull()
        if (userId != null) {
            val drugLikes = DrugLike.getDrugLikesByUser(userId)
            if (drugLikes.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "No drug likes found for user ID: $userId")
            } else {
                call.respond(HttpStatusCode.OK, drugLikes)
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
        }
    }
    suspend fun getDrugLikesByUser() {
        val userId = call.parameters["userId"]?.toIntOrNull()
        if (userId != null) {
            val drugLikes = DrugLike.getDrugLikesByUser(userId)
            if (drugLikes.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "No drug likes found for user ID: $userId")
            } else {
                val drugs = drugLikes.map { Drugs.getDrugById(it.drugIdLike) }.filterNotNull()
                call.respond(HttpStatusCode.OK, drugs)
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
        }
    }

    suspend fun getDrugCategoryByDrugId() {
        val drugId = call.parameters["drugId"]?.toIntOrNull()
        if (drugId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid drug ID")
            return
        }
        val drugCategory = Drugs.getCategoryByDrugId(drugId)
        if (drugCategory != null) call.respond(HttpStatusCode.OK, drugCategory)
        else call.respond(HttpStatusCode.NotFound, "Drug category not found for ID $drugId")
    }

    suspend fun getDrugInstructionByDrugId() {
        val drugId = call.parameters["drugId"]?.toIntOrNull()
        if (drugId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid drug ID")
            return
        }
        val instructions = Drugs.getDrugInstructionsByDrugId(drugId)
        if (instructions != null) call.respond(HttpStatusCode.OK, instructions)
        else call.respond(HttpStatusCode.NotFound, "No instructions found for drug ID $drugId")
    }

    suspend fun getAllDrugCategories() {
        val drugCategories = DrugCategories.getAllDrugCategories()
        call.respond(drugCategories)
    }
    suspend fun getAllDrugsByCategory() {
        val categoryId = call.parameters["drugCategoryId"]?.toIntOrNull()
        if (categoryId != null) {
            val drugs = Drugs.getDrugsByCategory(categoryId)
            if (drugs.isNotEmpty()) {
                call.respond(drugs)
            } else {
                call.respond(HttpStatusCode.NotFound, "No drugs found for this category")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid category ID")
        }
    }

    suspend fun getAllDrugInstructions() {
        val drugInstructions = DrugInstructions.getAllDrugInstructions()
        call.respond(drugInstructions)
    }

    suspend fun getDrugById() {
        val drugId = call.parameters["drugId"]?.toIntOrNull()
        if (drugId != null) {
            val drug = Drugs.getDrugById(drugId)
            if (drug != null) {
                call.respond(drug)
            } else {
                call.respond(HttpStatusCode.NotFound, "Drug not found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid drug ID")
        }
    }

    suspend fun getDrugCategoryById() {
        val drugCategoryId = call.parameters["drugCategoryId"]?.toIntOrNull()
        if (drugCategoryId != null) {
            val drugCategoryName = DrugCategories.getDrugCategoryById(drugCategoryId)
            if (drugCategoryName != null) {
                call.respond(drugCategoryName)
            } else {
                call.respond(HttpStatusCode.NotFound, "Drug category not found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid category ID")
        }
    }


    suspend fun getDrugInstructionById() {
        val drugInstructionId = call.parameters["drugInstructionId"]?.toIntOrNull()
        if (drugInstructionId != null) {
            val instruction = DrugInstructions.getDrugInstructionById(drugInstructionId)
            if (instruction != null) {
                call.respond(instruction)
            } else {
                call.respond(HttpStatusCode.NotFound, "Drug instruction not found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid drug instruction ID")
        }
    }

    suspend fun getAllDrugs() {
        val drugs = Drugs.getAllDrugs()
        call.respond(drugs)
    }
}