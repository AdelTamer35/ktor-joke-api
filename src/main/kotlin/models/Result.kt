package com.iadel.joke.models

import io.ktor.http.HttpStatusCode

sealed class JokeResult {
    data class Success(val joke: String) : JokeResult()
    data class Failure(val error: JokeError) : JokeResult()
}

sealed class JokeError(
    val status: HttpStatusCode,
    val message: String
) {
    object MissingParameter : JokeError(
        HttpStatusCode.BadRequest,
        "Please provide a 'word' query parameter."
    )

    object Unauthorized : JokeError(
        HttpStatusCode.Unauthorized,
        "AI Service authentication failed. Please check configuration."
    )

    object QuotaExceeded : JokeError(
        HttpStatusCode.TooManyRequests,
        "معلش، خلصنا نكت النهاردة من كتر الضحك! جرب كمان شوية صغيرين."
    )

    object ServiceUnavailable : JokeError(
        HttpStatusCode.ServiceUnavailable,
        "AI service is currently busy or timed out. Please try again later."
    )

    data class Unexpected(val internalMessage: String) : JokeError(
        HttpStatusCode.InternalServerError,
        "An unexpected error occurred. Our team has been notified."
    )
}
