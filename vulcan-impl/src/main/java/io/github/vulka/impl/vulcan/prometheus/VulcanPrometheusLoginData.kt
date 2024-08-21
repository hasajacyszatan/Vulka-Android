package io.github.vulka.impl.vulcan.prometheus

import io.github.vulka.core.api.LoginData
import io.github.vulka.impl.vulcan.hebe.login.HebeKeystore

class VulcanPrometheusLoginData(
    val login: String,
    val password: String,
    val deviceModel: String,
    val accessToken: String? = null,
) : LoginData()