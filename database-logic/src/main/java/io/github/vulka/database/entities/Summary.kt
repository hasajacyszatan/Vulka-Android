package io.github.vulka.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vulka.core.api.types.Summary
import java.util.UUID

@Entity
data class Summary(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @Embedded
    val summary: Summary,
    val semester: Int,
    val credentialsId: UUID
)
