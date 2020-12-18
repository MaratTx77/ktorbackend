package com.example.beans

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

// Читаем из реестра конфиг и создаем из него объекты, хранящие настройки
object AppConfig {

    private val config = ConfigFactory
        .parseString(this::class.java.classLoader.getResource("settings.conf").readText())

    val dbConfig = config.extract<DbSettings>("db")
    val jwtConfig = config.extract<JwtSettings>("jwt")
    val proxyConfig = config.extract<ProxySettings>("proxy")
}

data class ProxySettings(
    val isActive: Boolean,
    val addr: String,
    val port: Int,
    val type: String)

data class DbSettings(
    val jdbcUrl: String,
    val dbUser: String,
    val dbPassword: String)

data class JwtSettings(
    val secret: String,
    val issuer: String,
    val accessTokenTimeMin: Int,
    val refreshTokenTimeDays: Int)
