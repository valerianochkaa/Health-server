package com.example.features.token

import com.example.database.tokens.TokenDTO
import com.example.database.tokens.Tokens
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureTokenRouting() {
    routing {
        post("/token/check") {
            TokenController(call).checkToken()
        }
    }
}
