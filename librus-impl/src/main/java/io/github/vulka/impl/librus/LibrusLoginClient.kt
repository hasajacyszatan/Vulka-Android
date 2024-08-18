package io.github.vulka.impl.librus

import io.github.vulka.core.api.LoginClient
import io.github.vulka.core.api.LoginCredentials
import io.github.vulka.core.api.LoginData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.parameters

class LibrusLoginClient : LoginClient {
    private val client = HttpClient(OkHttp) {
        install(HttpCookies)
        BrowserUserAgent()
    }

    override suspend fun login(data: LoginData): LoginCredentials {
        val loginData = data as LibrusLoginData

        client.get("https://api.librus.pl/OAuth/Authorization?client_id=46&response_type=code&scope=mydata")

        val response = client.submitForm(
            url = "https://api.librus.pl/OAuth/Authorization?client_id=46",
            formParameters = parameters {
                append("action", "login")
                append("login", loginData.login)
                append("pass", loginData.password)
            }
        )

        if (response.status.value != 200) {
            throw Exception("Login failed\nResponse: " + response.body<String>())
        }

        client.get("https://api.librus.pl/OAuth/Authorization/2FA?client_id=46")

        val cookies = client.cookies("https://synergia.librus.pl")

        return LibrusLoginCredentials(
            cookies = cookies,
            request = loginData
        )
    }
}
