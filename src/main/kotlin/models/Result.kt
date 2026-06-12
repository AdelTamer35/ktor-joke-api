package com.iadel.joke.models

/**
 * A sealed class to represent the result of an operation.
 */
sealed class JokeResult {
    data class Success(val joke: String) : JokeResult()
    data class Failure(val error: JokeError) : JokeResult()
}

/**
 * Domain errors for the Joke application.
 */
sealed class JokeError {
    object MissingParameter : JokeError()
    object Unauthorized : JokeError()
    object QuotaExceeded : JokeError()
    object ServiceUnavailable : JokeError()
    data class Unexpected(val message: String) : JokeError()
}
