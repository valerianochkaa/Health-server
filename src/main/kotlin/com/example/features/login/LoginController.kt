package com.example.features.login

import com.example.database.tokens.TokenDTO
import com.example.database.tokens.Tokens
import com.example.database.users.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

// Обработчик логики входа пользователя, принимает текущий HTTP-запрос и ответ.
class LoginController(private val call: ApplicationCall) {
    suspend fun performLogin() {
        // Получение информации о входе пользователя
        val receive = call.receive<LoginReceiveRemote>()
        // Ищем пользователя в БД
        val userDTO = Users.findUserByEmail(receive.email)

        if (userDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
        } else {
            // Если пароли совпали, генерируем новый токен и добавляем в БД
            if (userDTO.userPassword == receive.password) {
                val token = UUID.randomUUID().toString()
                Tokens.insertToken(
                    TokenDTO(
                        tokenId = UUID.randomUUID().toString(),
                        tokenEmail = receive.email,
                        token = token
                    )
                )
                call.respond(LoginResponseRemote(token = token))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid password")
            }
        }
    }
}
