package io.github.vulka.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class LuckyNumber(
    /**
     * ID corresponds to credentials id.
     */
    @PrimaryKey
    val id: UUID,
    val number: Int
)
