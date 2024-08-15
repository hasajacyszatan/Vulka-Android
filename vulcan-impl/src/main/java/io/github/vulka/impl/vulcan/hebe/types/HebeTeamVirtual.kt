package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeTeamVirtual(
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("Shortcut")
    val shortcut: String,
    @SerialName("Name")
    val name: String,
    @SerialName("PartType")
    val partType: String
)
