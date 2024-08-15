package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeLuckyNumber(
    @SerialName("Day")
    val day: String,
    @SerialName("Number")
    val number: Int
)
