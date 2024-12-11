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
    fun insertUserAndGetId(usersDTO: UsersDTO): Int {
        return transaction {
            insert {
                it[userEmail] = usersDTO.userEmail
                it[userPassword] = usersDTO.userPassword
            } get userId
        }
    }
    fun findUserByEmail(email: String): UsersDTO? {
        return try {
            transaction {
                Users.select { userEmail eq email }
                    .mapNotNull {
                        UsersDTO(
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

