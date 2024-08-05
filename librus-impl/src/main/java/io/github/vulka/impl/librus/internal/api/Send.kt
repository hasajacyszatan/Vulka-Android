package io.github.vulka.impl.librus.internal.api

import android.util.Log
import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.applyCookie
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

private val json = Json {
    // we don't use all keys returned by the API, so we can ignore the rest
    ignoreUnknownKeys = true
    // Removes JSON specification restriction
    // e.g. allow String type for numbers in json
    isLenient = true
}

internal suspend inline fun <reified T> LibrusUserClient.apiGET(
    endpoint: String
): T {
    Log.d("LIBRUS API", "GET: /$endpoint")
    val response = client.get("https://synergia.librus.pl/gateway/api/2.0/$endpoint") {
        credentials.cookies.forEach {
            applyCookie(it)
        }
    }
    val body = response.bodyAsText()
    Log.d("LIBRUS API", "Response: $body")
    return json.decodeFromString(body)
}
