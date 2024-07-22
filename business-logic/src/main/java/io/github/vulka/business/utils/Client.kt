package io.github.vulka.business.utils

import com.google.gson.Gson
import io.github.vulka.core.api.Features
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.UserClient
import io.github.vulka.impl.librus.LibrusFeatures
import io.github.vulka.impl.librus.LibrusLoginCredentials
import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.vulcan.HebeFeatures
import io.github.vulka.impl.vulcan.VulcanLoginCredentials
import io.github.vulka.impl.vulcan.VulcanUserClient

fun getUserClient(
    platform: Platform,
    credentials: String
): UserClient {
    val client = when (platform) {
        Platform.Vulcan -> {
            val loginData = Gson().fromJson(credentials, VulcanLoginCredentials::class.java)
            VulcanUserClient(loginData)
        }
        Platform.Librus -> {
            val loginData = Gson().fromJson(credentials, LibrusLoginCredentials::class.java)
            LibrusUserClient(loginData)
        }
    }

    return client
}

fun getFeaturesByPlatform(platform: Platform): Features {
    return when (platform) {
        Platform.Vulcan -> HebeFeatures()
        Platform.Librus -> LibrusFeatures()
    }
}