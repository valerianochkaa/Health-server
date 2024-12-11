package com.example.features.token

import com.example.database.tokens.TokenDTO
import com.example.database.tokens.Tokens
import com.example.database.users.UsersDTO
import com.example.database.users.Users
import com.example.untils.isValidEmail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class TokenController(private val call: ApplicationCall) {
    suspend fun checkToken() {
        val tokenDTO = call.receive<TokenDTO>()
        val isValid = Tokens.isTokenValid(tokenDTO.token)
        val response = TokenRemote(
            valid = isValid,
            message = if (isValid) "Token is valid." else "Token is invalid."
        )
        call.respond(HttpStatusCode.OK, response)
    }
}