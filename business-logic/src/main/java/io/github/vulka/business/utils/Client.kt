package io.github.vulka.business.utils

import io.github.vulka.core.api.Features
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.UserClient
import io.github.vulka.impl.librus.LibrusFeatures
import io.github.vulka.impl.librus.LibrusLoginCredentials
import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.vulcan.HebeFeatures
import io.github.vulka.impl.vulcan.VulcanLoginCredentials
import io.github.vulka.impl.vulcan.VulcanUserClient
import kotlinx.serialization.json.Json

fun getUserClient(
    platform: Platform,
    credentials: String
): UserClient {
    return when (platform) {
        Platform.VulcanHebe -> {
            val decodedCredentials = Json.decodeFromString<VulcanLoginCredentials>(credentials)
            VulcanUserClient(decodedCredentials)
        }
        Platform.Librus -> {
            val decodedCredentials = Json.decodeFromString<LibrusLoginCredentials>(credentials)
            LibrusUserClient(decodedCredentials)
        }
    }
}

fun getFeaturesByPlatform(platform: Platform): Features {
    return when (platform) {
        Platform.VulcanHebe -> HebeFeatures()
        Platform.Librus -> LibrusFeatures()
    }
}
