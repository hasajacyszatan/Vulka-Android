package io.github.vulka.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.vulka.database.entities.Summary
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface SummaryDao : BaseDao<Summary> {
    @Query("DELETE FROM summary WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM summary WHERE credentialsId = :id AND semester = :semester")
    fun getBySemesterAndCredentialsId(semester: Int, id: UUID): Flow<List<Summary>>
}