package io.github.vulka.impl.librus.api

import android.util.Log
import io.github.vulka.core.api.ApiException
import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.api.common.SynergiaApiError
import io.github.vulka.impl.librus.applyCookie
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

internal const val LibrusApiUrl = "https://api.librus.pl"
internal const val LibrusSynergiaUrl = "https://synergia.librus.pl/gateway/api/2.0"

private val json = Json {
    // we don't use all keys returned by the API, so we can ignore the rest
    ignoreUnknownKeys = true
    // Removes JSON specification restriction
    // e.g. allow String type for numbers in json
    isLenient = true
}

internal suspend inline fun <reified T> LibrusUserClient.getSynergiaApi(endpoint: String): T {
    Log.d("LIBRUS API", "GET: $endpoint")

    val response = client.get("$LibrusSynergiaUrl$endpoint") {
        credentials.cookies.forEach {
            applyCookie(it)
        }
    }

    val body = response.bodyAsText()
    Log.d("LIBRUS API", "Response: $body")

    if (response.status.value >= 400) {
        val error = json.decodeFromString<SynergiaApiError>(body)
        throw ApiException(error.message)
    }

    return json.decodeFromString(body)
}
