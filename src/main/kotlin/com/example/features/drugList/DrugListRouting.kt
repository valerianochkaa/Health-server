package com.example.features.drugList

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureDrugListRouting() {
    routing {
        route("/drugs") {
            get {
                val controller = DrugListController(call)
                controller.getAllDrugs()
            }
            get("/categories") {
                val controller = DrugListController(call)
                controller.getAllDrugCategories()
            }
            get("/categories/{drugCategoryId}") {
                val controller = DrugListController(call)
                controller.getAllDrugsByCategory()
            }
            get("/instructions") {
                val controller = DrugListController(call)
                controller.getAllDrugInstructions()
            }
            get("/likes/{userId}") {
                val controller = DrugListController(call)
                controller.getAllDrugLikes()
            }
            get("/likesDrug/{userId}") {
                val controller = DrugListController(call)
                controller.getDrugLikesByUser()
            }
        }
        route("/drug") {
            get("/{drugId}") {
                val controller = DrugListController(call)
                controller.getDrugById()
            }
            get("/category/{drugCategoryId}") {
                val controller = DrugListController(call)
                controller.getDrugCategoryById()
            }
            get("/categoryByDrugId/{drugId}") {
                val controller = DrugListController(call)
                controller.getDrugCategoryByDrugId()
            }
            get("/instruction/{drugInstructionId}") {
                val controller = DrugListController(call)
                controller.getDrugInstructionById()
            }
            get("/instructionByDrugId/{drugId}") {
                val controller = DrugListController(call)
                controller.getDrugInstructionByDrugId()
            }
            post("/insertLike/{drugId}"){
                val controller = DrugListController(call)
                controller.insertLike()
            }
            delete("/deleteLike/{drugId}") {
                val controller = DrugListController(call)
                controller.deleteLikeByDrugId()
            }
        }
    }
}


