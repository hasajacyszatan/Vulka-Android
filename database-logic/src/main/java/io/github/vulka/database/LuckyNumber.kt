package io.github.vulka.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class LuckyNumber(
    @PrimaryKey
    val credentialsId: UUID,
    val number: Int
)
