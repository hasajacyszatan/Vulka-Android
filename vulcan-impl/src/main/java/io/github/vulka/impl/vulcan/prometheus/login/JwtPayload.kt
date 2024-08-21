package io.github.vulka.impl.vulcan.prometheus.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JwtPayload(
    val name: String,
    val uid: String,
    val tenant: String,
    @SerialName("unituid")
    val unitUId: String,
    val uri: String,
    val service: Boolean,
    val nbf: Long,
    val exp: Long,
    val iat: Long
)
