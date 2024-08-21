package io.github.vulka.impl.vulcan.prometheus.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryUserInfoResponse(
    @SerialName("ShowCaptcha")
    val showCaptcha: Boolean,
    @SerialName("ExtraMessage")
    val extraMessage: String,
)