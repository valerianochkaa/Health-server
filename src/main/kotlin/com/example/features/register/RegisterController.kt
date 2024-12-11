package com.example.features.register

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

class RegisterController(private val call: ApplicationCall) {
    suspend fun registerNewUser() {
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>()
        if (!registerReceiveRemote.email.isValidEmail()) {
            call.respond(HttpStatusCode.BadRequest, "Email is not valid")
            return
        }
        val token = UUID.randomUUID().toString()
        try {
            transaction {
                val userByEmail = Users.findUserByEmail(registerReceiveRemote.email)
                if (userByEmail != null) {
                    throw IllegalArgumentException("User with this email already exists")
                }
                Users.insertUserAndGetId(
                    UsersDTO(
                        userEmail = registerReceiveRemote.email,
                        userPassword = registerReceiveRemote.password
                    )
                )
                Tokens.insertToken(
                    TokenDTO(
                        tokenId = UUID.randomUUID().toString(),
                        tokenEmail = registerReceiveRemote.email,
                        token = token
                    )
                )
            }
            call.respond(RegisterResponseRemote(token = token))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.Conflict, e.message ?: "User already exists")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Can't create user: ${e.localizedMessage}")
        }
    }
}