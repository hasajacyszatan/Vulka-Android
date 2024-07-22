package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MeetingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meetings: Meetings)

    @Update
    suspend fun update(meetings: Meetings)

    @Query("DELETE FROM meetings WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM meetings WHERE credentialsId = :credentialsId")
    fun getByCredentialsId(credentialsId: UUID): Flow<List<Meetings>>
}