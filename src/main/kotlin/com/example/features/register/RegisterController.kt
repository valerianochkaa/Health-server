package com.example.features.register

import com.example.database.pressures.PressuresDTO
import com.example.database.pressures.Pressures
import com.example.database.temperatures.TemperaturesDTO
import com.example.database.temperatures.Temperatures
import com.example.database.tokens.TokenDTO
import com.example.database.tokens.Tokens
import com.example.database.users.UserDTO
import com.example.database.users.Users
import com.example.database.weights.WeightsDTO
import com.example.database.weights.Weights
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

        val token = UUID.randomUUID().toString() // Генерация токена вне блока try-catch

        try {
            transaction {
                // Проверка существования пользователя по email
                val userByEmail = Users.findUserByEmail(registerReceiveRemote.email)
                if (userByEmail != null) {
                    throw IllegalArgumentException("User with this email already exists")
                }

                // Вставка нового пользователя
                Users.insertUserAndGetId(
                    UserDTO(
                        userEmail = registerReceiveRemote.email,
                        userPassword = registerReceiveRemote.password // Пароль лучше хешировать перед сохранением
                    )
                )

                // Вставка токена
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


// Вставка связанных записей
//                Pressures.insertPressure(
//                    PressuresDTO(
//                        userId = userId,
//                        upperValue = null,
//                        lowerValue = null,
//                        pulseValue = null,
//                        recordDate = null
//                    )
//                )
//                Temperatures.insertTemperature(
//                    TemperaturesDTO(
//                        userId = userId,
//                        temperatureValue = null,
//                        recordDate = null
//                    )
//                )
//                Weights.insertWeight(
//                    WeightsDTO(
//                        userId = userId,
//                        weightValue = null,
//                        recordDate = null
//                    )
//                )