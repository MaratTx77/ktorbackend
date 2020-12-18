package com.example.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.beans.AppConfig
import com.example.models.UserPrincipal
import java.util.*

object JwtConfig {
    private val secret = AppConfig.jwtConfig.secret
    private val issuer = AppConfig.jwtConfig.issuer
    private val validityMsAccessToken = AppConfig.jwtConfig.accessTokenTimeMin * 600_000
    private val validityMsRefreshToken = AppConfig.jwtConfig.refreshTokenTimeDays * 36_000_00 * 24

    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    // tokens
    fun generateAccessToken(userPrincipal: UserPrincipal): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", userPrincipal.id)
        .withClaim("name", userPrincipal.name)
        .withClaim("email", userPrincipal.email)
        .withExpiresAt(getAccessExpiration())  // ttl
        .sign(algorithm)

    fun generateRefreshToken(userPrincipal: UserPrincipal): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", userPrincipal.id)
        .withClaim("name", userPrincipal.name)
        .withClaim("email", userPrincipal.email)
        .withExpiresAt(getRefreshExpiration())
        .sign(algorithm)

    private fun getAccessExpiration() = Date(System.currentTimeMillis() + validityMsAccessToken)
    private fun getRefreshExpiration() = Date(System.currentTimeMillis() + validityMsRefreshToken)
}
