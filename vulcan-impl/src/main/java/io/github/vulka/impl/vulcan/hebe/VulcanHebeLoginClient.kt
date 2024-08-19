package io.github.vulka.impl.vulcan.hebe

import io.github.vulka.core.api.LoginClient
import io.github.vulka.core.api.LoginCredentials
import io.github.vulka.core.api.LoginData

class VulcanHebeLoginClient : LoginClient {
    private val api = VulcanHebeApi()

    override suspend fun login(data: LoginData): LoginCredentials {
        val loginData = data as VulcanHebeLoginData
        val response = api.register(loginData.keystore, loginData.symbol, loginData.token, loginData.pin)

        return VulcanHebeLoginCredentials(response,data.keystore)
    }
}