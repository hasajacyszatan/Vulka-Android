package io.github.vulka.core.api.types

import java.time.LocalDateTime

data class Meeting(
    val topic: String,
    val dateTime: LocalDateTime,
    val place: String?,
    // Not sure what it does, exists in Vulcan
    val agenda: String?
)