package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class HebeMeeting(
    @SerialName("Id")
    val id: Int,
    @SerialName("When")
    val `when`: HebeDate,
    @SerialName("Where")
    val where: String,
    @SerialName("Why")
    val why: String,
    @SerialName("Agenda")
    val agenda: String,
    @SerialName("AdditionalInfo")
    @Contextual
    val additionalInfo: JsonElement?, // Don't know type for this moment
    @SerialName("Online")
    @Contextual
    val online: String? // Don't know type for this moment
)
