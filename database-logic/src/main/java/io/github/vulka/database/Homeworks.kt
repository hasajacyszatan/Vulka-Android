package io.github.vulka.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vulka.core.api.types.Homework
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class Homeworks(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @Embedded
    val homework: Homework,
    val lastSync: LocalDateTime,
    val credentialsId: UUID
)