package io.github.vulka.core.api.types

import java.time.LocalDate

data class Note(
    // e.g "Uwaga negatywna" or "Pochwała"
    val name: String,
    val content: String,
    val points: String?,
    val creator: String,
    val date: LocalDate,
)