
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.iadel.joke"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "com.iadel.joke.MainKt"
}

kotlin {
    jvmToolchain(21)
}

ktor {
    fatJar {
        archiveFileName.set("joke-ktor.jar")
    }
}

dependencies {
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.statusPages)
    implementation(libs.logback.classic)


    // Ktor Client for making API calls
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio) // Engine for the client
    implementation(ktorLibs.client.contentNegotiation) // To handle JSON in requests
    implementation(ktorLibs.client.serialization) // To parse Gemini's JSON response

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
