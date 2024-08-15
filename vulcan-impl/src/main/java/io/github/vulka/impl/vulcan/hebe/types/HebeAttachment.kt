package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeAttachment(
    @SerialName("Name")
    val name: String,
    @SerialName("Link")
    val link: String
)
