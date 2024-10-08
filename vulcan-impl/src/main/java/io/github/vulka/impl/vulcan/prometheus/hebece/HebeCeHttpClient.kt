package io.github.vulka.impl.vulcan.prometheus.hebece

import io.github.vulka.impl.vulcan.hebe.*
import io.github.vulka.impl.vulcan.hebe.login.HebeKeystore
import io.github.vulka.impl.vulcan.hebe.types.ApiResponse
import io.github.vulka.impl.vulcan.prometheus.hebece.types.ApiRequest
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

class HebeCeHttpClient(private val keystore: HebeKeystore) {
    companion object {
        const val APP_NAME = "DzienniczekPlus 3.0"
        const val APP_VERSION = "24.07.32 (G)"
        const val APP_OS = "Android"
        const val APP_USER_AGENT = "Dart/3.3 (dart:io)"
    }

    internal val client = HttpClient(OkHttp)

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private fun getEncodedPath(fullUrl: String): String {
        val pattern = Pattern.compile("api/mobile/.+")
        val matcher = pattern.matcher(fullUrl)
        if (!matcher.find()) {
            throw IllegalArgumentException("The URL does not seem correct (does not match `(api/mobile/.+)` regex)")
        }
        return URLEncoder.encode(matcher.group(), "UTF-8").lowercase()
    }

    fun buildHeaders(fullUrl: String, body: String? = null): Map<String, String> {
        val date = Date.from(ZonedDateTime.now().toInstant())
        val time = SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss", Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }.format(date) + " GMT"
        val (_,fingerprint,privateKey) = keystore.getData()

        val (digest, canonicalUrl, signature) = getSignatureValues(fingerprint,privateKey, body, fullUrl,date )

        val headers = mutableMapOf(
            "user-agent" to APP_USER_AGENT,
            "vos" to APP_OS,
            "vdevicemodel" to keystore.deviceModel,
            "vapi" to "1",
            "vdate" to time,
            "vcanonicalurl" to canonicalUrl,
            "signature" to signature,
            "vversioncode" to "612"
        )

        body?.let {
            headers["Digest"] = digest
            headers["Content-Type"] = "application/json"
        }
        return headers
    }

    internal fun <T> buildPayload(body: T): ApiRequest<T> {
        return ApiRequest(
            appName = APP_NAME,
            appVersion = APP_VERSION,
            envelope = body,
            firebaseToken = keystore.firebaseToken,
            api = 1,
            requestId = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            timestampFormatted = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
        )
    }

    internal inline fun <reified V,reified T> post(url: String, body: V, query: Map<String, String>? = null): T? = runBlocking {
        val urlBuilder = URLBuilder(url)

        query?.forEach { (key, value) ->
            urlBuilder.parameters.append(key, value)
        }

        val builtUrl = urlBuilder.buildString()

        val payload = buildPayload(body)
        val payloadString = Json.encodeToString<ApiRequest<V>>(payload)
        val headers = buildHeaders(url, payloadString)

        val response: HttpResponse = client.post(builtUrl) {
            headers {
                headers.forEach { (key, value) -> append(key, value) }
            }
            contentType(ContentType.Application.Json)
            setBody(payloadString)
        }

        val apiResponse = json.decodeFromString<ApiResponse<T>>(response.bodyAsText())
        checkErrors(apiResponse)

        return@runBlocking apiResponse.envelope
    }

    internal inline fun <reified T> get(url: String, query: Map<String, String>? = null): T? = runBlocking {
        val urlBuilder = URLBuilder(url)

        query?.forEach { (key, value) ->
            urlBuilder.parameters.append(key, value)
        }

        val builtUrl = urlBuilder.buildString()
        val headers = buildHeaders(builtUrl)

        val response: HttpResponse = client.get(builtUrl) {
            headers {
                headers.forEach { (key, value) -> append(key, value) }
            }
        }

        val apiResponse = json.decodeFromString<ApiResponse<T>>(response.bodyAsText())

        checkErrors(apiResponse)

        return@runBlocking apiResponse.envelope
    }

    private fun checkErrors(apiResponse: ApiResponse<*>) {
        if (apiResponse.status.code == 100 && apiResponse.status.message.contains(": "))
            throw InvalidSignatureValuesException()

        when (apiResponse.status.code) {
            200 -> {
                throw InvalidTokenException(apiResponse.status.message)
            }
            108 -> {
                throw UnauthorizedCertificateException(apiResponse.status.message)
            }
            203 -> {
                throw InvalidPINException(apiResponse.status.message)
            }
            204 -> {
                throw ExpiredTokenException(apiResponse.status.message)
            }
            -1 -> {
                throw InvalidSymbolException(apiResponse.status.message)
            }
        }

        if (apiResponse.status.code != 0) {
            throw VulcanAPIException(apiResponse.status.message)
        }
    }
}
