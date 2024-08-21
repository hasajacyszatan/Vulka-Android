package io.github.vulka.impl.librus.api.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdField(
    @SerialName("Id")
    val id: Int
)
