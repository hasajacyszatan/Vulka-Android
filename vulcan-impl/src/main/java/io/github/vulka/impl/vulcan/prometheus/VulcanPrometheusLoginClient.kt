package io.github.vulka.impl.vulcan.prometheus

import io.github.vulka.core.api.LoginClient
import io.github.vulka.core.api.LoginCredentials
import io.github.vulka.core.api.LoginData
import io.github.vulka.impl.vulcan.prometheus.login.ApiApResponse
import io.github.vulka.impl.vulcan.prometheus.login.ApiResponse
import io.github.vulka.impl.vulcan.prometheus.login.QueryUserInfoResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.fleeksoft.ksoup.Ksoup

class VulcanPrometheusLoginClient : LoginClient {
    private val client: HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        BrowserUserAgent()
    }

    private suspend fun getLoginPage(): String {
        return client.get("https://eduvulcan.pl/logowanie").bodyAsText()
    }

    private suspend fun queryUserInfo(username: String): ApiResponse<QueryUserInfoResponse> {
        return client.post("https://eduvulcan.pl/Account/QueryUserInfo") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("alias", username)
            }))
        }.body<ApiResponse<QueryUserInfoResponse>>()
    }

    private suspend fun loginToPage(username: String, password: String, csrfToken: String): String {
        return client.post("https://eduvulcan.pl/logowanie") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("Alias", username)
                append("Password", password)
                append("captchaUser", "")
                append("__RequestVerificationToken", csrfToken)
            }))
        }.bodyAsText()
    }

    private suspend fun getApiAp(): String {
        return client.get("https://eduvulcan.pl/api/ap").bodyAsText()
    }

    override suspend fun login(data: LoginData): LoginCredentials {
        val loginData = data as VulcanPrometheusLoginData

        if (loginData.accessToken == null) {
            val loginPageHTML = getLoginPage()
            val csrfToken = Ksoup.parse(loginPageHTML).select("input[name=\"__RequestVerificationToken\"]")
                .attr("value")

            val queryUserInfoResponse = queryUserInfo(loginData.login)

            if (queryUserInfoResponse.data?.showCaptcha == false) {
                loginToPage(
                    username = loginData.login,
                    password = loginData.password,
                    csrfToken = csrfToken
                )

                val apiApPage = getApiAp()
                val apiApResponse = Json.decodeFromString<ApiApResponse>(Ksoup.parse(apiApPage)
                    .select("#ap").attr("value"))

                if (apiApResponse.tokens.isEmpty())
                    throw PrometheusNoStudentsException()

                return VulcanPrometheusLoginCredentials(
                    login = loginData.login,
                    password = loginData.password,
                    accessToken = apiApResponse.accessToken,
                    deviceModel = loginData.deviceModel
                )
            } else {
                // TODO: Handle captcha
                throw PrometheusCaptchaException()
            }
        } else {
            return VulcanPrometheusLoginCredentials(
                login = loginData.login,
                password = loginData.password,
                accessToken = loginData.accessToken,
                deviceModel = loginData.deviceModel
            )
        }
    }
}