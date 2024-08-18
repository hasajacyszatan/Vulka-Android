package io.github.vulka.impl.librus

import io.github.vulka.core.api.LoginCredentials
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class LibrusLoginCredentials(
    @Transient
    val cookies: List<Cookie> = emptyList(),
    val request: LibrusLoginData
) : LoginCredentials()
