package com.example.capital_taxi.data.source.remote

import com.google.android.datatransport.runtime.logging.Logging
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import okhttp3.internal.http2.Http2Reader.Companion.logger
import io.ktor.client.plugins.logging.*
object RegisterKtorClient {
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
        install(Logging) { // إضافة Logging
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
}
