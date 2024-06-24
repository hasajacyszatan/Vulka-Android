package io.github.vulka.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vulka.core.api.types.Semester
import java.util.UUID

@Entity
data class Semesters(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @Embedded
    val semester: Semester,
    val credentialsId: UUID
)
