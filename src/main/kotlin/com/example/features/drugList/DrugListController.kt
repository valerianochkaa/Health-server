package com.example.features.drugList

import com.example.database.drugs.DrugsDTO
import com.example.database.drugs.Drugs
import com.example.database.drugsInstrucions.DrugInstructionsDTO
import com.example.database.drugsInstrucions.DrugInstructions
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DrugListController(private val call: ApplicationCall) {
    suspend fun getAllDrugs() {
        val drugs = Drugs.getAllDrugs()
        call.respond(drugs)
    }

    suspend fun getDrugById() {
        val drugId = call.parameters["drugId"]?.toIntOrNull()
        if (drugId != null) {
            val drug = Drugs.getDrugById(drugId)
            if (drug != null) call.respond(drug)
            else call.respond(HttpStatusCode.NotFound, "Drug not found")
        } else call.respond(HttpStatusCode.BadRequest, "Invalid drug ID")
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