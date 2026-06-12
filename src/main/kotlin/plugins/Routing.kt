package com.iadel.joke.plugins

import com.iadel.joke.routes.jokeRoutes
import com.iadel.joke.services.JokeService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    val jokeService = JokeService()

    routing {
        jokeRoutes(jokeService)
    }
}