package com.iadel.joke.clients


import com.iadel.joke.models.Content
import com.iadel.joke.models.GeminiRequest
import com.iadel.joke.models.GeminiResponse
import com.iadel.joke.models.Part
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Custom exception types to classify Gemini failures without leaking details.
 */
class GeminiUnauthorizedException : Exception()
class GeminiQuotaExceededException : Exception()
class GeminiServiceUnavailableException : Exception()

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

                HttpStatusCode.Unauthorized -> throw GeminiUnauthorizedException()
                HttpStatusCode.Forbidden -> throw GeminiUnauthorizedException()
                HttpStatusCode.TooManyRequests -> throw GeminiQuotaExceededException()
                HttpStatusCode.ServiceUnavailable, HttpStatusCode.GatewayTimeout -> throw GeminiServiceUnavailableException()
                else -> throw Exception("Unexpected API status: ${httpResponse.status}")
            }
        } catch (e: HttpRequestTimeoutException) {
            throw GeminiServiceUnavailableException()
        } catch (e: ConnectTimeoutException) {
            throw GeminiServiceUnavailableException()
        } catch (e: SocketTimeoutException) {
            throw GeminiServiceUnavailableException()
        }
    }
}
