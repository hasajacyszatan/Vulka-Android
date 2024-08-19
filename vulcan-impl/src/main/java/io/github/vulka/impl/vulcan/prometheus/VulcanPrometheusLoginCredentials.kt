package io.github.vulka.impl.vulcan.prometheus

import io.github.vulka.core.api.LoginCredentials
import io.github.vulka.impl.vulcan.hebe.login.HebeKeystore
import io.github.vulka.impl.vulcan.hebe.types.HebeAccount
import kotlinx.serialization.Serializable

@Serializable
data class VulcanPrometheusLoginCredentials(
    // Save for future backend migrations
    val login: String,
    val password: String,
    val account: HebeAccount,
    val keystore: HebeKeystore
) : LoginCredentials()