package io.github.vulka.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vulka.core.api.types.Meeting
import java.util.UUID

@Entity
class Meetings(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @Embedded
    val meeting: Meeting,
    val credentialsId: UUID
)