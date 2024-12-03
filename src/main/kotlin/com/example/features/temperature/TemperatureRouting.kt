package com.example.features.temperature

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureTemperatureRouting() {
    routing {
        routing {
            route("/temperatures") {
                get {
                    val controller = TemperatureController(call)
                    controller.getAllTemperatures()
                }
                get("/user/{userId}") {
                    val controller = TemperatureController(call)
                    controller.getAllTemperaturesByUser()
                }
            }
            route("/temperature") {
                get("/{temperatureId}") {
                    val controller = TemperatureController(call)
                    controller.getTemperatureById()
                }
                post("/insert"){
                    val controller = TemperatureController(call)
                    controller.insertTemperatureAndGetId()
                }
                delete("/delete/{temperatureId}") {
                    val controller = TemperatureController(call)
                    controller.deleteTemperatureById()
                }
            }
        }
    }
}