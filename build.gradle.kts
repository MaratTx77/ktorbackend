

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.3.70"
    id("org.flywaydb.flyway") version "6.5.5"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "com.example"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

flyway   {
    url = "jdbc:postgresql://localhost:5432/ktor" // System.getenv("DB_URL") если указаны в docker Compose
    user = "ktorUser" // System.getenv("DB_USER") если указаны в docker Compose
    password = "ktorUser" // System.getenv("DB_PASSWORD") если указаны в docker Compose
    baselineOnMigrate=true
    locations = arrayOf("filesystem:resources/db/migration") // место файлов миграции
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")


    implementation( "io.github.config4k:config4k:0.4.2")
    implementation( "com.zaxxer:HikariCP:3.4.5")
    implementation( "org.postgresql:postgresql:42.2.16")
    implementation( "org.jetbrains.exposed:exposed:0.17.7")
    implementation( "org.flywaydb:flyway-core:6.5.5")

    implementation("com.google.api-client:google-api-client:1.30.10")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}