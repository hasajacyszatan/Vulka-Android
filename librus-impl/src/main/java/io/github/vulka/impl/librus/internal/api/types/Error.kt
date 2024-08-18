package io.github.vulka.impl.librus.internal.api.types

import kotlinx.serialization.Serializable

@Serializable
data class LibrusError(
    val status: String,
    val errors: List<LibrusErrorData>
)

@Serializable
data class LibrusErrorData(
    val code: Int,
    val message: String
)
