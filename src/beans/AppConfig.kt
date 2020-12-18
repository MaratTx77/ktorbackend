//jdbcUrl = "jdbc:postgresql://localhost:5432/ktor"
//dbUser = "ktoruser"
//dbPassword = "ktorUser"


package com.example.beans

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

// Читаем из реестра конфиг и создаем из него объекты, хранящие настройки
object AppConfig {

    private val config = ConfigFactory
        .parseString(this::class.java.classLoader.getResource("settings.conf").readText())

    val dbConfig : DbSettings = //config.extract<DbSettings>("db")
        DbSettings(
            System.getenv("KTOR_JDBC_URL"),
            System.getenv("KTOR_DB_USER"),
            System.getenv("KTOR_DB_PASSWORD"))
    private val jwtConfigShort = config.extract<JwtSettingsShort>("jwt")
    val jwtConfig = JwtSettings( // hide secret from kotlin source to system environment
        System.getenv("KTOR_JWT_SECRET"),
        jwtConfigShort.issuer,
        jwtConfigShort.accessTokenTimeMin,
        jwtConfigShort.refreshTokenTimeDays
    )
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

data class JwtSettingsShort(
    val issuer: String,
    val accessTokenTimeMin: Int,
    val refreshTokenTimeDays: Int)


data class JwtSettings(
    val secret: String,
    val issuer: String,
    val accessTokenTimeMin: Int,
    val refreshTokenTimeDays: Int)
