package io.github.vulka.impl.librus

import io.github.vulka.core.api.LoginData
import kotlinx.serialization.Serializable

@Serializable
class LibrusLoginData(
    val login: String,
    val password: String
) : LoginData()
