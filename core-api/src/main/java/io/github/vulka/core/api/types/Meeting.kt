package io.github.vulka.core.api.types

import java.time.LocalDate

data class Meeting(
    val topic: String,
    val date: LocalDate,
    val place: String?,
    // Not sure what it does, exists in Vulcan
    val agenda: String?
)