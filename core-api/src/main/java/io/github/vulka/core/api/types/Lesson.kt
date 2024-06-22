package io.github.vulka.core.api.types

import java.time.LocalDate

enum class LessonChangeType {
    Canceled,
    Replacement,
}

data class Lesson(
    val subjectName: String,
    val position: Int,
    val room: String? = null,
    val teacherName: String,
    val groupName: String? = null,
    val change: LessonChange? = null,

    val date: LocalDate,
    val startTime: String,
    val endTime: String,
)

data class LessonChange(
    val type: LessonChangeType,
    val newSubjectName: String?,
    val newTeacherName: String?,
    val message: String? = null,
    val room: String? = null,
)
