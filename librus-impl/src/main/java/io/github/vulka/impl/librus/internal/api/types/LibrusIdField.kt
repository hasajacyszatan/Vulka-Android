package io.github.vulka.impl.librus.internal.api.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LibrusIdField(
    @SerialName("Id")
    val id: String
)
