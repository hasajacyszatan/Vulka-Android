package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface SemestersDao : BaseDao<Semesters> {
    @Query("DELETE FROM semesters WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM semesters WHERE credentialsId = :id")
    fun getByCredentialsId(id: UUID): Flow<List<Semesters>>
}
