package io.github.vulka.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.vulka.database.entities.Meetings
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MeetingsDao : BaseDao<Meetings> {
    @Query("DELETE FROM meetings WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM meetings WHERE credentialsId = :credentialsId")
    fun getByCredentialsId(credentialsId: UUID): Flow<List<Meetings>>
}