package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.util.UUID

@Dao
interface LuckyNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(luckyNumber: LuckyNumber)

    @Update
    suspend fun update(luckyNumber: LuckyNumber)

    @Query("SELECT * FROM luckynumber WHERE id = :id LIMIT 1")
    fun get(id: UUID): LuckyNumber?
}
