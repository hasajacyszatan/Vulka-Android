package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface LuckyNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(luckyNumber: LuckyNumber)

    @Update
    suspend fun update(luckyNumber: LuckyNumber)

    @Query("DELETE FROM luckynumber WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM luckynumber WHERE credentialsId = :credentialsId LIMIT 1")
    fun get(credentialsId: UUID): Flow<LuckyNumber?>
}
