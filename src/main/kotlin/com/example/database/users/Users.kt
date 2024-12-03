package com.example.database.users

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table("users") {
    val userId = integer("userId").autoIncrement()
    val userEmail = varchar("userEmail", 50)
    val userPassword = varchar("userPassword", 50)

    override val primaryKey = PrimaryKey(userId, name = "PK_User_ID")

    fun insertUserAndGetId(userDTO: UserDTO): Int {
        return transaction {
            insert {
                it[userEmail] = userDTO.userEmail
                it[userPassword] = userDTO.userPassword
            } get userId
        }
    }

    fun findUserByEmail(email: String): UserDTO? {
        return try {
            transaction {
                Users.select { userEmail eq email }
                    .mapNotNull {
                        UserDTO(
                            userId = it[userId],
                            userEmail = it[userEmail],
                            userPassword = it[userPassword]
                        )
                    }
                    .singleOrNull()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

