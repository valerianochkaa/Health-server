package com.example.features.weight

import com.example.features.temperature.TemperatureController
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureWeightRouting() {
    routing {
        routing {
            route("/weights") {
                get {
                    val controller = WeightController(call)
                    controller.getAllWeights()
                }
                get("/user/{userId}") {
                    val controller = WeightController(call)
                    controller.getAllWeightsByUser()
                }
            }
            route("/weight") {
                get("/{weightId}") {
                    val controller = WeightController(call)
                    controller.getWeightById()
                }
                post("/insert"){
                    val controller = WeightController(call)
                    controller.insertWeightAndGetId()
                }
                delete("/delete/{weightId}") {
                    val controller = WeightController(call)
                    controller.deleteWeightById()
                }
            }
        }
    }
}