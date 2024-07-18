package io.github.vulka.impl.vulcan.hebe

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.vulka.impl.vulcan.hebe.login.HebeKeystore
import io.github.vulka.impl.vulcan.hebe.types.ApiRequest
import io.github.vulka.impl.vulcan.hebe.types.ApiResponse
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

class HebeHttpClient(private val keystore: HebeKeystore) {
    companion object {
        const val APP_NAME = "DzienniczekPlus 2.0"
        const val APP_VERSION = "24.06.03 (G)"
        const val APP_OS = "Android"
        const val APP_USER_AGENT = "Dart/3.3 (dart:io)"
    }

    private val client = HttpClient(OkHttp)

    private fun getEncodedPath(fullUrl: String): String {
        val pattern = Pattern.compile("api/mobile/.+")
        val matcher = pattern.matcher(fullUrl)
        if (!matcher.find()) {
            throw IllegalArgumentException("The URL does not seem correct (does not match `(api/mobile/.+)` regex)")
        }
        return URLEncoder.encode(matcher.group(), "UTF-8").lowercase()
    }

    private fun buildHeaders(fullUrl: String, body: String? = null): Map<String, String> {
        val date = Date.from(ZonedDateTime.now().toInstant())
        val time = SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss", Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }.format(date) + " GMT"
        val (_,fingerprint,privateKey) = keystore.getData()

        val (digest, canonicalUrl, signature) = getSignatureValues(fingerprint,privateKey, body, fullUrl,date )

        val headers = mutableMapOf(
            "User-Agent" to APP_USER_AGENT,
            "vOS" to APP_OS,
            "vDeviceModel" to keystore.deviceModel,
            "vAPI" to "1",
            "vDate" to time,
            "vCanonicalUrl" to canonicalUrl,
            "Signature" to signature
        )

        body?.let {
            headers["Digest"] = digest
            headers["Content-Type"] = "application/json"
        }
        return headers
    }

    private fun buildPayload(body: Any): ApiRequest {
        val (_,fingerprint,_) = keystore.getData()
        return ApiRequest(
            appName = APP_NAME,
            appVersion = APP_VERSION,
            certificateId = fingerprint,
            envelope = body,
            firebaseToken = keystore.firebaseToken,
            api = 1,
            requestId = UUID.randomUUID(),
            timestamp = System.currentTimeMillis(),
            timestampFormatted = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
        )
    }

    fun <T> post(url: String, body: Any, clazz: Class<T>): T? = runBlocking {
        val payload = buildPayload(body)
        val payloadString = Gson().toJson(payload)
        val headers = buildHeaders(url, payloadString)

        val response: HttpResponse = client.post(url) {
            headers {
                headers.forEach { (key, value) -> append(key, value) }
            }
            contentType(ContentType.Application.Json)
            setBody(payloadString)
        }

        val type = TypeToken.getParameterized(ApiResponse::class.java, clazz).type
        val responseBody = response.bodyAsText()
        val apiResponse = Gson().fromJson<ApiResponse<T>>(responseBody, type)

        checkErrors(apiResponse)

        return@runBlocking apiResponse.envelope
    }

    fun <T> get(url: String, clazz: Class<T>, query: Map<String, String>? = null): T? = runBlocking {
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

        val responseBody = response.bodyAsText()

        val type = TypeToken.getParameterized(ApiResponse::class.java, clazz).type
        val apiResponse = Gson().fromJson<ApiResponse<T>>(responseBody, type)

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
