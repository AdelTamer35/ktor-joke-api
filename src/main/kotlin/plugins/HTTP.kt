package com.iadel.joke.plugins

import com.iadel.joke.exceptions.JokeException
import com.iadel.joke.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureHTTP() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(status, ErrorResponse("The requested path was not found."))
        }

        exception<JokeException> { call, cause ->
            call.respond(cause.status, ErrorResponse(cause.message))
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Something went wrong on our end: ${cause.localizedMessage}")
            )
        }
    }
}
