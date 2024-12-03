package com.example.features.drugList

import com.example.database.drugs.Drugs
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureDrugListRouting() {
    routing {
        route("/drugs") {
            get {
                val controller = DrugListController(call)
                controller.getAllDrugs()
            }


            get("/{drugId}") {
                val controller = DrugListController(call)
                controller.getDrugById()
            }
        }
    }
}

//            post("/add") {
//                val controller = DrugListController(call)
//                controller.addItemToDrug()
//            }
//            post("/updateInstruction") {
//                val controller = DrugListController(call)
//                controller.updateInstruction()
//            }
//            get("/search") {
//                val query = call.parameters["query"]?.lowercase() ?: ""
//                if (query.isBlank()) {
//                    call.respond(HttpStatusCode.BadRequest, "Query parameter is missing or empty")
//                    return@get
//                }
//                val drugListController = DrugListController(call)
//                drugListController.searchItemToDrugList(query)
//            }
//            get("/searchByType") {
//                val type = call.parameters["type"]?.lowercase() ?: ""
//                if (type.isBlank()) {
//                    call.respond(HttpStatusCode.BadRequest, "Type parameter is missing or empty")
//                    return@get
//                }
//                val drugListController = DrugListController(call)
//                drugListController.searchByTypeItemToDrugList(type)
//            }
//
//            get("/searchByLikesStatus") {
//                val likesStatusParam = call.parameters["likesStatus"]?.lowercase()
//                if (likesStatusParam.isNullOrBlank()) {
//                    call.respond(HttpStatusCode.BadRequest, "Likes status parameter is missing or empty")
//                    return@get
//                }
//
//                // Преобразуем строку в логическое значение
//                val likesStatus = when (likesStatusParam) {
//                    "true" -> true
//                    "false" -> false
//                    else -> {
//                        call.respond(HttpStatusCode.BadRequest, "Likes status must be 'true' or 'false'")
//                        return@get
//                    }
//                }
//
//                val drugListController = DrugListController(call)
//                drugListController.searchByLikesStatusItemToDrugList(likesStatus)
//            }


