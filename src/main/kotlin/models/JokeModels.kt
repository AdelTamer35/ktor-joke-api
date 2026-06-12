package com.iadel.joke.models

import kotlinx.serialization.Serializable

@Serializable
data class JokeResponse(
    val joke: String
)

@Serializable
data class ErrorResponse(
    val error: String
)