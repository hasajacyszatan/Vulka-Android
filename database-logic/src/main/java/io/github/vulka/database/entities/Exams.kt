package io.github.vulka.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vulka.core.api.types.Exam
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class Exams(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @Embedded
    val exam: Exam,
    val lastSync: LocalDateTime,
    val credentialsId: UUID
)