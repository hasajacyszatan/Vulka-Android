package io.github.vulka.impl.vulcan.prometheus

import io.github.vulka.core.api.LoginClient
import io.github.vulka.core.api.LoginCredentials
import io.github.vulka.core.api.LoginData

class VulcanPrometheusLoginClient : LoginClient {
    override suspend fun login(data: LoginData): LoginCredentials {
        TODO("Not yet implemented")
    }
}