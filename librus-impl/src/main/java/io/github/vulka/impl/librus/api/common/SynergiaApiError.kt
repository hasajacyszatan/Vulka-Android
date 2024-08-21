package io.github.vulka.impl.librus.api.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SynergiaApiError(
//    @SerialName("Status")
//    val status: String,
    @SerialName("Code")
    val code: String,
    @SerialName("Message")
    val message: String
)
