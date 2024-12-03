package com.example.database.tokens

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object Tokens : Table() {
    val tokenId = varchar("tokenId", 100)
    val tokenEmail = varchar("tokenEmail", 100)
    val token = varchar("token", 100)

    fun insertToken(tokenDTO: TokenDTO) {
        transaction {
            insert {
                it[tokenId] = tokenDTO.tokenId
                it[tokenEmail] = tokenDTO.tokenEmail
                it[token] = tokenDTO.token
            }
        }
    }
}

