package io.github.vulka.business.utils

import io.github.vulka.core.api.Features
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.UserClient
import io.github.vulka.impl.librus.LibrusFeatures
import io.github.vulka.impl.librus.LibrusLoginCredentials
import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.vulcan.hebe.HebeFeatures
import io.github.vulka.impl.vulcan.hebe.VulcanHebeLoginCredentials
import io.github.vulka.impl.vulcan.hebe.VulcanHebeUserClient
import io.github.vulka.impl.vulcan.prometheus.VulcanPrometheusLoginCredentials
import io.github.vulka.impl.vulcan.prometheus.VulcanPrometheusUserClient
import kotlinx.serialization.json.Json

fun getUserClient(
    platform: Platform,
    credentials: String
): UserClient {
    return when (platform) {
        Platform.VulcanHebe -> {
            val decodedCredentials = Json.decodeFromString<VulcanHebeLoginCredentials>(credentials)
            VulcanHebeUserClient(decodedCredentials)
        }
        Platform.Librus -> {
            val decodedCredentials = Json.decodeFromString<LibrusLoginCredentials>(credentials)
            LibrusUserClient(decodedCredentials)
        }

        Platform.VulcanPrometheus -> {
            val decodedCredentials = Json.decodeFromString<VulcanPrometheusLoginCredentials>(credentials)
            VulcanPrometheusUserClient(decodedCredentials)
        }
    }
}

fun getFeaturesByPlatform(platform: Platform): Features {
    return when (platform) {
        Platform.VulcanHebe -> HebeFeatures()
        Platform.VulcanPrometheus -> HebeFeatures()
        Platform.Librus -> LibrusFeatures()
    }
}
