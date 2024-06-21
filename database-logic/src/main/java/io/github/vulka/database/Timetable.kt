package io.github.vulka.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vulka.core.api.types.Lesson
import java.time.LocalDate
import java.util.UUID

@Entity
data class Timetable(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @Embedded
    val lesson: Lesson,
    val lessonDate: LocalDate,
    val lastSync: LocalDate,
    val credentialsId: UUID
)