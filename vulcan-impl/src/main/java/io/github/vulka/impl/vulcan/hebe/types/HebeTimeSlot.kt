package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeTimeSlot(
    @SerialName("Id")
    val id: Int,
    @SerialName("Start")
    val from: String,
    @SerialName("End")
    val to: String,
    @SerialName("Display")
    val displayedTime: String,
    @SerialName("Position")
    val position: Int
)
