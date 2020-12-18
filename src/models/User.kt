package com.example.models

import io.ktor.auth.*
import org.jetbrains.exposed.dao.*
import org.joda.time.DateTime

// Reflection to db table
object Users: IntIdTable("users") {
    val email = varchar("email", 255).nullable().uniqueIndex()
    val userName = varchar("username", 255)
    val photourl = varchar("photourl", 255).nullable()
    val googleId = varchar("googleid",100).nullable()
    val created = datetime("created").default(DateTime.now())
    val active = bool("active").default(true)
}

// Dao for users table
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<User> (Users) {
        // static methods
        fun addGoogleUser(name: String, googleId: String, pictureUrl: String, email: String?) = User.new {
            this.email = email
            this.userName = name
            this.photourl = pictureUrl
            this.googleId = googleId
            this.active = true
        }
    }

    var email by Users.email
    var userName by Users.userName
    var photourl by Users.photourl
    var googleId by Users.googleId
    var created by Users.created
    var active by Users.active

    fun toUserPrincipal() = UserPrincipal(id.toString(), userName, email ?: "none")
}

data class UserPrincipal(
    val id: String,
    val name: String,
    val email: String
): Principal


