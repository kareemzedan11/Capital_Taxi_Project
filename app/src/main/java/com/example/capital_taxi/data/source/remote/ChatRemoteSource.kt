package com.example.capital_taxi.data.source.remote


import com.example.capital_taxi.domain.passenger.model.ChatRequest
import com.example.capital_taxi.domain.passenger.model.ChatResponse
import com.example.capital_taxi.utils.Constants.ApiConstants
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.ContentType.Application.Json
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import com.google.gson.Gson
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.http.contentType


object KtorClient {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            gson {
                setLenient()
                serializeNulls()
            }
        }
    }
}

suspend fun sendChatMessage(message: String): String {
    val client = KtorClient.client
    val url = ApiConstants.CHAT_API_URL

    return try {
        val response: HttpResponse = client.post(url) {
            contentType(Json)
            setBody(ChatRequest(message))
        }

        val responseBody = response.bodyAsText()
        val gson = Gson()
        val chatResponse = gson.fromJson(responseBody, ChatResponse::class.java)
        chatResponse.response
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}
