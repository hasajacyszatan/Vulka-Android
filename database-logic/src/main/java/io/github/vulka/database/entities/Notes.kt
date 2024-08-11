package io.github.vulka.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vulka.core.api.types.Note
import java.util.UUID

@Entity
data class Notes(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @Embedded
    val note: Note,
    val credentialsId: UUID
)
