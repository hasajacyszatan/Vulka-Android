package io.github.vulka.impl.vulcan.prometheus.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ApiResponse<T> {
    @SerialName("success")
    val success: Boolean = true

    @SerialName("data")
    val data: T? = null
}
