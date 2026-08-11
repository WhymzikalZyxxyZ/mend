package xyz.zyxwonderland.mend.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Single shared HTTP client for the app. Currently only used by [xyz.zyxwonderland.mend.update.UpdateChecker];
 * the MEND Worker calls described in docs/architecture/overview.md will reuse this once built.
 */
object MendHttpClient {
    val instance: HttpClient by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5_000
                connectTimeoutMillis = 5_000
            }
        }
    }
}
