package io.github.vulka.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.vulka.database.entities.LuckyNumber
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface LuckyNumberDao : BaseDao<LuckyNumber> {
    @Query("DELETE FROM luckynumber WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM luckynumber WHERE credentialsId = :credentialsId LIMIT 1")
    fun get(credentialsId: UUID): Flow<LuckyNumber?>
}
