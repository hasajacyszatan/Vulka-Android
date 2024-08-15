package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeLessonChanges(
    @SerialName("Id")
    val id: Int,
    @SerialName("Type")
    val type: Int,
    @SerialName("Separation")
    val separation: Boolean
)
