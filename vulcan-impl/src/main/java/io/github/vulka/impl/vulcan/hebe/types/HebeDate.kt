package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeDate(
    @SerialName("Timestamp")
    val timestamp: Long,
    @SerialName("Date")
    val date: String,
    @SerialName("Time")
    val time: String
)
