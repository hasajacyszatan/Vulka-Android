package io.github.vulka.impl.librus.api.login

import io.github.vulka.core.api.ApiException
import io.github.vulka.impl.librus.api.LibrusApiUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.Cookie
import io.ktor.http.parameters

internal class LoginApi(private val client: HttpClient) {
    suspend fun login(login: String, password: String): List<Cookie> {
        // initialize (idk for what)
        client.get("$LibrusApiUrl/OAuth/Authorization?client_id=46&response_type=code&scope=mydata")

        val response = client.submitForm(
            url = "$LibrusApiUrl/OAuth/Authorization?client_id=46",
            formParameters = parameters {
                append("action", "login")
                append("login", login)
                append("pass", password)
            }
        )

        val responseBody = response.body<LoginResponse>()

        // handle error
        if (responseBody.status == LoginStatus.ERROR) {
            throw ApiException(
                responseBody.errors!!.joinToString { it.message }
            )
        }

        // alternative responseBody.goTo
        client.get("$LibrusApiUrl/OAuth/Authorization/2FA?client_id=46")

        return client.cookies("https://synergia.librus.pl")
    }
}
