package subha.app.cyra.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * The ONLY network client in the app. It talks exclusively to our own Cloud Functions
 * endpoints (the Gemini chat proxy in `feature/aichat`, PDF generation in
 * `feature/reports`) - never to Gemini or any third-party API directly, per the
 * product doc's security requirement. Firebase traffic goes through the GitLive SDK,
 * not through this client.
 *
 * [tokenProvider] fetches the current Firebase ID token (from GitLive's
 * `Firebase.auth.currentUser?.getIdToken()`) and is supplied by whatever feature module
 * first needs an authenticated call - kept as a lambda here so `core` doesn't depend on
 * `core.firebase`.
 */
fun createHttpClient(tokenProvider: suspend () -> String? = { null }): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true; isLenient = true })
    }
    install(Logging) {
        level = LogLevel.INFO
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
    }
    install(Auth) {
        bearer {
            loadTokens {
                tokenProvider()?.let { BearerTokens(accessToken = it, refreshToken = "") }
            }
        }
    }
}
