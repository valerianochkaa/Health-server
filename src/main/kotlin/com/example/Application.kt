package com.example

import com.example.features.login.configureLoginRouting
import com.example.features.pressure.configurePressureRouting
import com.example.features.register.configureRegisterRouting
import com.example.features.temperature.configureTemperatureRouting
import com.example.features.weight.configureWeightRouting
import com.example.features.drugList.configureDrugListRouting
import com.example.features.token.configureTokenRouting
import com.example.plugins.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    Database.connect("jdbc:postgresql://localhost:5432/health_db", driver = "org.postgresql.Driver",
        user = "postgres",  password = "159753")
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureLoginRouting()
    configureRegisterRouting()
    configureTokenRouting()

    configureWeightRouting()
    configureTemperatureRouting()
    configurePressureRouting()

    configureDrugListRouting()
}
