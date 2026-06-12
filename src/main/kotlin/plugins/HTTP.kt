package com.iadel.joke.plugins

import com.iadel.joke.models.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond


fun Application.configureHTTP() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(status, ErrorResponse("The requested path was not found."))
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Something went wrong on our end: ${cause.localizedMessage}")
            )
        }
    }
}
