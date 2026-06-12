package com.iadel.joke.services

import com.iadel.joke.clients.GeminiClient
import com.iadel.joke.exceptions.AiServiceException
import com.iadel.joke.exceptions.QuotaExceededException
import com.iadel.joke.exceptions.UnauthorizedException
import com.iadel.joke.models.JokeError
import com.iadel.joke.models.JokeResult
import com.iadel.joke.prompts.JokePromptBuilder

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
        } catch (e: UnauthorizedException) {
            JokeResult.Failure(JokeError.Unauthorized)
        } catch (e: QuotaExceededException) {
            JokeResult.Failure(JokeError.QuotaExceeded)
        } catch (e: AiServiceException) {
            JokeResult.Failure(JokeError.ServiceUnavailable)
        } catch (e: Exception) {
            JokeResult.Failure(JokeError.Unexpected(e.message ?: "Unknown Error"))
        }
    }
}
