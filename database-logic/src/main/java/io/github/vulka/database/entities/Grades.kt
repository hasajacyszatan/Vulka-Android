package io.github.vulka.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vulka.core.api.types.Grade
import java.util.UUID

@Entity
data class Grades(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @Embedded
    val grade: Grade,
    val semester: Int,
    val credentialsId: UUID
)
