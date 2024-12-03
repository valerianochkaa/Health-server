package com.example.features.drugList

import com.example.database.drugCategory.DrugCategory
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
        val drugCategories = DrugCategory.getAllDrugCategories()
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
            val drugCategoryName = DrugCategory.getDrugCategoryById(drugCategoryId)
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


//    suspend fun addItemToDrug() {
//        try {
//            // Извлечение данных из тела запроса
//            val drugsDTO = call.receive<DrugsDTO>()
//            // Вставка записи в таблицу Drugs и получение ID
//            val drugId = Drugs.insertDrugsAndGetId(drugsDTO)
//
//            // Вставка записи в таблицу Drugs и получение ID
//            val drugName = Drugs.insertDrugsAndGetName(drugsDTO)
//
//            // Отправка успешного ответа с ID добавленного лекарства
//            call.respond(HttpStatusCode.Created, "Drug added")
//
//            // Вставка связанных записей
//            DrugInstructions.insertDrugsInstructionsAndGetId(
//                DrugInstructionsDTO(
//                    drugName = drugName,
//                    compositionDescription = null,
//                    releaseForm = null,
//                    contraindications = null,
//                    indicationsForUse = null,
//                    overdose = null,
//                    pharmacodynamics = null,
//                    pharmacokinetics = null,
//                    pregnancy = null,
//                    prescriptionRequirements = null,
//                    sideEffects = null,
//                    storageConditions = null,
//                    doses = null
//                )
//            )
//
//        } catch (e: Exception) {
//            // Обработка ошибок и отправка соответствующего ответа
//            call.respond(HttpStatusCode.BadRequest, "Error adding drug: ${e.message}")
//        }
//    }
//
//    suspend fun updateInstruction() {
//        try {
//            // Извлечение идентификатора инструкции из параметров запроса
//            val instructionId = call.parameters["instructionId"]?.toIntOrNull()
//                ?: throw IllegalArgumentException("Invalid instruction ID")
//
//            // Извлечение данных для обновления из тела запроса
//            val updateDTO = call.receive<DrugInstructionsDTO>()
//
//            // Проверка на попытку обновления имени
//            if (updateDTO.drugName != null) {
//                throw IllegalArgumentException("Updating the drug name is not allowed")
//            }
//
//            // Обновление записи в базе данных
//            transaction {
//                DrugInstructions.update({DrugInstructions.instructionId eq instructionId}) {
//                    it[compositionDescription] = updateDTO.compositionDescription
//                    it[releaseForm] = updateDTO.releaseForm
//                    it[contraindications] = updateDTO.contraindications
//                    it[indicationsForUse] = updateDTO.indicationsForUse
//                    it[overdose] = updateDTO.overdose
//                    it[pharmacodynamics] = updateDTO.pharmacodynamics
//                    it[pharmacokinetics] = updateDTO.pharmacokinetics
//                    it[pregnancy] = updateDTO.pregnancy
//                    it[prescriptionRequirements] = updateDTO.prescriptionRequirements
//                    it[sideEffects] = updateDTO.sideEffects
//                    it[storageConditions] = updateDTO.storageConditions
//                    it[doses] = updateDTO.doses
//                }
//            }
//
//            // Отправка успешного ответа
//            call.respond(HttpStatusCode.OK, "Instruction updated successfully")
//        } catch (e: Exception) {
//            // Обработка ошибок и отправка соответствующего ответа
//            call.respond(HttpStatusCode.BadRequest, "Error updating instruction: ${e.message}")
//        }
//    }
//
//    suspend fun searchItemToDrugList(query: String) {
//        try {
//            val drugs = transaction {
//                Drugs.select {
//                    (Drugs.drugName like "%$query%") or
//                            (Drugs.typeName like "%$query%") or
//                            (Drugs.drugAnalog like "%$query%")or
//                            (Drugs.drugPrice like "%$query%")
//                }.map {
//                    DrugsDTO(
//                        drugId = it[Drugs.drugId],
//                        typeName = it[Drugs.typeName],
//                        likesStatus = it[Drugs.likesStatus],
//                        drugName = it[Drugs.drugName],
//                        drugPrice = it[Drugs.drugPrice],
//                        drugAnalog = it[Drugs.drugAnalog]
//                    )
//                }
//            }
//            if (drugs.isEmpty()) {
//                call.respond(HttpStatusCode.NotFound, "No drugs found for query: $query")
//            } else {
//                call.respond(drugs)
//            }
//        } catch (e: Exception) {
//            call.respond(HttpStatusCode.InternalServerError, "Failed to search drugs: ${e.localizedMessage}")
//        }
//    }
//
//    suspend fun searchByTypeItemToDrugList(type: String) {
//        try {
//            val drugs = transaction {
//                //  выбирает записи из таблицы Drugs, где поле typeName равно переданному параметру type.
//                Drugs.select { Drugs.typeName eq type }.map {
//                    DrugsDTO(
//                        drugId = it[Drugs.drugId],
//                        typeName = it[Drugs.typeName],
//                        likesStatus = it[Drugs.likesStatus],
//                        drugName = it[Drugs.drugName],
//                        drugPrice = it[Drugs.drugPrice],
//                        drugAnalog = it[Drugs.drugAnalog]
//                    )
//                }
//            }
//            if (drugs.isEmpty()) {
//                call.respond(HttpStatusCode.NotFound, "No drugs found for type: $type")
//            } else {
//                call.respond(drugs)
//            }
//        } catch (e: Exception) {
//            call.respond(HttpStatusCode.InternalServerError, "Failed to search drugs: ${e.localizedMessage}")
//        }
//    }
//
//    suspend fun searchByLikesStatusItemToDrugList(likesStatus: Boolean) {
//        if (!likesStatus) {
//            call.respond(HttpStatusCode.BadRequest, "Likes status must be true to retrieve drugs.")
//            return
//        }
//
//        try {
//            val drugs = transaction {
//                // выбирает записи из таблицы Drugs, где поле likesStatus равно true.
//                Drugs.select { Drugs.likesStatus eq true }.map {
//                    DrugsDTO(
//                        drugId = it[Drugs.drugId],
//                        typeName = it[Drugs.typeName],
//                        likesStatus = it[Drugs.likesStatus],
//                        drugName = it[Drugs.drugName],
//                        drugPrice = it[Drugs.drugPrice],
//                        drugAnalog = it[Drugs.drugAnalog]
//                    )
//                }
//            }
//
//            if (drugs.isEmpty()) {
//                call.respond(HttpStatusCode.NotFound, "No drugs found for likes status: true")
//            } else {
//                call.respond(drugs)
//            }
//        } catch (e: Exception) {
//            call.respond(HttpStatusCode.InternalServerError, "Failed to search drugs: ${e.localizedMessage}")
//        }
//    }