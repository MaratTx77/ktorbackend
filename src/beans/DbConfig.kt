package com.example.beans

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DbConfig {

    fun init() {
        Database.connect(hikari())

        // db versioning migration
        val flyway = Flyway.configure().dataSource(
            AppConfig.dbConfig.jdbcUrl,
            AppConfig.dbConfig.dbUser,
            AppConfig.dbConfig.dbPassword)
            .load()
        flyway.migrate()
    }

    // Db pool
    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = AppConfig.dbConfig.jdbcUrl
        config.username = AppConfig.dbConfig.dbUser
        config.password = AppConfig.dbConfig.dbPassword
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    // wrap to non-blocking query
    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}
