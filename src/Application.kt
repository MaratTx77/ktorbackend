package com.example

import com.example.beans.AppConfig
import com.example.beans.DbConfig
import com.example.beans.DbConfig.dbQuery
import com.example.config.HttpClient
import com.example.config.JsonConverter
import io.ktor.application.*
import com.example.config.JwtConfig
import com.example.models.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import com.fasterxml.jackson.databind.*
import io.ktor.client.features.*
import io.ktor.features.*
import io.ktor.jackson.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            realm = AppConfig.jwtConfig.issuer
            validate {
                val id = it.payload.getClaim("id").asString()
                val name = it.payload.getClaim("name").asString()
                val email = it.payload.getClaim("email").asString()
                if(id != null && name != null){
                    UserPrincipal(id, name, email)
                }else{
                    null
                }
            }
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    DbConfig.init()

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/client") {
            //call.respondText(HttpClient.test1())
            call.respondText(JsonConverter.toMap("{\"name\":\"mkyong\", \"age\":\"37\"}").toString())
        }

        get("/html-dsl") {
//            dbQuery {
//                Word.new {
//                    english = "one"
//                    russian = "один"
//                    partOfSpeech = PartOfSpeech.NUMERAL
//                }
//            }

//            dbQuery {
//                Vocabulary.new {
//                    name="test"
//                    description = "test2"
//                }
//            }

            var z: Int = 55
            dbQuery {
                val v = Vocabulary.get(1)
                z = v.words.count()
            }

            call.respondHtml {



                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n $z" }
                        }
                    }
                }
            }
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        post("/login") {
            try {
                val loginInfo = call.receive<LoginInfo>()

                if (loginInfo.type.equals("ANDROID")) {
                    val responseString =
                        //HttpClient.test1()
                        HttpClient.getStringRequest("https://oauth2.googleapis.com/tokeninfo?id_token=${loginInfo.id}")

                    val gMap = JsonConverter.toMap(responseString)
                    val userName = gMap["name"] ?: "unknown"
                    val googleId = gMap["sub"]
                    val photoUrl = gMap["picture"] ?: "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"
                    val email = gMap["email"]

                    googleId?.let {
                        val user = dbQuery {
                            User.find { Users.googleId eq googleId}.elementAtOrNull(0) ?:
                                User.addGoogleUser(userName, googleId, photoUrl, email)
                        }

                        call.respond(
                            JwtAuth(
                                accessToken = JwtConfig.generateAccessToken(user.toUserPrincipal()),
                                refreshToken = JwtConfig.generateRefreshToken(user.toUserPrincipal())
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                val errorInfo = when(e) {
                    is ClientRequestException -> "invalid auth" to "invalid path2 google auth"
                    is ServerResponseException -> "server error" to "error from google auth server"
                    else -> "unknown error" to "catch 500+ http error code from google"
                }
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(errorInfo.first,errorInfo.second))
            }
        }

        authenticate{// только авторизированные запросы
            get("/authenticate"){
                call.respond("get authenticated value from token " +
                        "name = ${call.user?.name}, password= ${call.user?.id}")
            }

            post("/refresh") {
                val user = call.principal<UserPrincipal>()!!
                call.respond(
                    JwtAuth(
                        accessToken = JwtConfig.generateAccessToken(user),
                        refreshToken = JwtConfig.generateRefreshToken(user)
                    ))
            }
        }
    }


}

val ApplicationCall.user get() = authentication.principal<UserPrincipal>()

