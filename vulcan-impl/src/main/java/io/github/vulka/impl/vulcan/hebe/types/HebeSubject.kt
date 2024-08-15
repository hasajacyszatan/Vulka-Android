package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeSubject(
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("Name")
    val name: String,
    @SerialName("Kod")
    val code: String,
    @SerialName("Position")
    val position: Int
)
