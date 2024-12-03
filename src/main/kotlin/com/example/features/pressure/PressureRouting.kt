package com.example.features.pressure

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configurePressureRouting() {
    routing {
        routing {
            route("/pressures") {
                get {
                    val controller = PressureController(call)
                    controller.getAllPressures()
                }
                get("/user/{userId}") {
                    val controller = PressureController(call)
                    controller.getAllPressuresByUser()
                }
            }
            route("/pressure") {
                get("/{pressureId}") {
                    val controller = PressureController(call)
                    controller.getPressureById()
                }
                post("/insert") {
                    val controller = PressureController(call)
                    controller.insertPressureAndGetId()
                }
                delete("/delete/{pressureId}") {
                    val controller = PressureController(call)
                    controller.deletePressureById()
                }
            }
        }
    }
}