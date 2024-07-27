package io.github.vulka.core.api.types

import java.time.LocalDate

data class Homework(
    val content: String,
    val dateCreated: LocalDate,
    val deadline: LocalDate,
    val creator: String,
    val subject: String,
    // If your platform not support attachments, set to empty list.
    val attachments: List<HomeworkAttachment>,
    // Vulcan supports sending answers to homework back to teacher, if your platform not supports it, set to false
    val isAnswerRequired: Boolean,
)

data class HomeworkAttachment(
    val name: String?,
    // In Vulcan attachments have the link to OneDrive,
    // if your platform contains data in other way, serialize it and put in this field.
    val data: String?
)