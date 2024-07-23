package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface NotesDao : BaseDao<Notes> {
    @Query("DELETE FROM notes WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM notes WHERE credentialsId = :credentialsId LIMIT 1")
    fun get(credentialsId: UUID): Flow<Notes>

    @Query("SELECT * FROM notes WHERE credentialsId = :credentialsId")
    fun getByCredentialsId(credentialsId: UUID): Flow<List<Notes>>
}