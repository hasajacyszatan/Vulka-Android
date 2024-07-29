package io.github.vulka.core.api.types

import java.time.LocalDate

data class Exam(
    val type: String?,
    val content: String,
    val dateCreated: LocalDate,
    val deadline: LocalDate,
    val creator: String,
    val subject: String
)