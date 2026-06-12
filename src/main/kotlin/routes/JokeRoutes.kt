package com.iadel.joke.routes

import com.iadel.joke.models.ErrorResponse
import com.iadel.joke.models.JokeError
import com.iadel.joke.models.JokeResponse
import com.iadel.joke.models.JokeResult
import com.iadel.joke.services.JokeService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.jokeRoutes(jokeService: JokeService) {
    get("/adel/joke") {
        val word = call.request.queryParameters["word"] ?: ""

        when (val result = jokeService.generateJoke(word)) {
            is JokeResult.Success -> {
                call.respond(JokeResponse(result.joke))
            }

            is JokeResult.Failure -> {
                val (status, message) = when (result.error) {
                    JokeError.MissingParameter ->
                        HttpStatusCode.BadRequest to "Please provide a 'word' query parameter."

                    JokeError.Unauthorized ->
                        HttpStatusCode.Unauthorized to "AI Service authentication failed. Please check configuration."

                    JokeError.QuotaExceeded ->
                        HttpStatusCode.TooManyRequests to "معلش، خلصنا نكت النهاردة من كتر الضحك! جرب كمان شوية صغيرين."

                    JokeError.ServiceUnavailable ->
                        HttpStatusCode.ServiceUnavailable to "AI service is currently busy or timed out. Please try again later."

                    is JokeError.Unexpected ->
                        HttpStatusCode.InternalServerError to "An unexpected error occurred. Our team has been notified."
                }

                call.respond(status, ErrorResponse(message))
            }
        }
    }
}
