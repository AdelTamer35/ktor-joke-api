package com.iadel.joke.clients

import com.iadel.joke.exceptions.AiServiceException
import com.iadel.joke.exceptions.QuotaExceededException
import com.iadel.joke.exceptions.UnauthorizedException
import com.iadel.joke.models.Content
import com.iadel.joke.models.GeminiRequest
import com.iadel.joke.models.GeminiResponse
import com.iadel.joke.models.Part
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class GeminiClient {

    private companion object {
        const val GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent"
        const val TIMEOUT_MILLIS = 30_000L
    }

    private val apiKey = System.getenv("GEMINI_API_KEY")
        ?: throw IllegalStateException("GEMINI_API_KEY environment variable is missing")

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MILLIS
            connectTimeoutMillis = TIMEOUT_MILLIS
            socketTimeoutMillis = TIMEOUT_MILLIS
        }
    }

    suspend fun generateContent(prompt: String): String {
        return try {
            val httpResponse = client.post("${GEMINI_URL}?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = prompt))))))
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val response: GeminiResponse = httpResponse.body()
                    response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?: throw Exception("Empty response from Gemini")
                }

                HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> 
                    throw UnauthorizedException()
                
                HttpStatusCode.TooManyRequests -> 
                    throw QuotaExceededException()
                
                HttpStatusCode.ServiceUnavailable, HttpStatusCode.GatewayTimeout -> 
                    throw AiServiceException()
                
                else -> throw Exception("Unexpected API status: ${httpResponse.status}")
            }
        } catch (e: HttpRequestTimeoutException) {
            throw AiServiceException()
        } catch (e: ConnectTimeoutException) {
            throw AiServiceException()
        } catch (e: SocketTimeoutException) {
            throw AiServiceException()
        }
    }
}
