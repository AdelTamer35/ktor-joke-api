package com.iadel.joke.services

import com.iadel.joke.clients.GeminiClient
import com.iadel.joke.clients.GeminiQuotaExceededException
import com.iadel.joke.clients.GeminiServiceUnavailableException
import com.iadel.joke.clients.GeminiUnauthorizedException
import com.iadel.joke.models.JokeError
import com.iadel.joke.models.JokeResult
import com.iadel.joke.prompts.JokePromptBuilder

/**
 * Orchestrator service that coordinates between prompt building and the AI client.
 * Returns a [JokeResult] to cleanly separate success from various failure types.
 */
class JokeService(
    private val promptBuilder: JokePromptBuilder = JokePromptBuilder(),
    private val aiClient: GeminiClient = GeminiClient()
) {
    suspend fun generateJoke(word: String): JokeResult {
        if (word.isBlank()) {
            return JokeResult.Failure(JokeError.MissingParameter)
        }

        return try {
            val prompt = promptBuilder.buildEgyptianJokePrompt(word)
            val joke = aiClient.generateContent(prompt)
            JokeResult.Success(joke)
        } catch (e: GeminiUnauthorizedException) {
            JokeResult.Failure(JokeError.Unauthorized)
        } catch (e: GeminiQuotaExceededException) {
            JokeResult.Failure(JokeError.QuotaExceeded)
        } catch (e: GeminiServiceUnavailableException) {
            JokeResult.Failure(JokeError.ServiceUnavailable)
        } catch (e: Exception) {
            // Log the actual internal message locally, but return a sanitized Unexpected error
            JokeResult.Failure(JokeError.Unexpected(e.message ?: "Unknown Error"))
        }
    }
}
