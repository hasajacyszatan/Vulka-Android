package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeLessonRoom(
    @SerialName("Id")
    val id: Int,
    @SerialName("Code")
    val code: String? = null
)
