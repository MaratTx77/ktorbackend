package com.example.models

data class JwtAuth(
    val accessToken: String,
    val refreshToken: String
)

data class LoginInfo(
    val type: String,
    val id: String
)

data class ErrorResponse(
    val error: String,
    val error_description : String
)
