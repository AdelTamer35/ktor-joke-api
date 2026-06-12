package com.iadel.joke.routes

import com.iadel.joke.models.ErrorResponse
import com.iadel.joke.models.JokeResponse
import com.iadel.joke.models.JokeResult
import com.iadel.joke.services.JokeService
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.jokeRoutes(jokeService: JokeService) {
    get("/adel/joke") {
        val word = call.request.queryParameters["word"] ?: ""

        when (val result = jokeService.generateJoke(word)) {
            is JokeResult.Success -> {
                call.respond(JokeResponse(result.joke))
            }

            is JokeResult.Failure -> {
                call.respond(result.error.status, ErrorResponse(result.error.message))
            }
        }
    }
}
