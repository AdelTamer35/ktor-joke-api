package com.iadel.joke.exceptions

import io.ktor.http.*

sealed class JokeException(
    val status: HttpStatusCode,
    override val message: String
) : Exception(message)

class AiServiceException(
    status: HttpStatusCode = HttpStatusCode.ServiceUnavailable,
    message: String = "AI service is currently busy or timed out. Please try again later."
) : JokeException(status, message)

class UnauthorizedException(
    message: String = "AI Service authentication failed. Please check configuration."
) : JokeException(HttpStatusCode.Unauthorized, message)

class QuotaExceededException(
    message: String = "معلش، خلصنا نكت النهاردة من كتر الضحك! جرب كمان شوية صغيرين."
) : JokeException(HttpStatusCode.TooManyRequests, message)

class BadRequestException(
    message: String = "Please provide a valid input."
) : JokeException(HttpStatusCode.BadRequest, message)
