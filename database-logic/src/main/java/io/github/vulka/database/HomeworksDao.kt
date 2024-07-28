package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

@Dao
interface HomeworksDao : BaseDao<Homeworks> {
    @Query("DELETE FROM homeworks WHERE credentialsId = :id AND DATE(deadline) BETWEEN DATE(:dateFrom) AND DATE(:dateTo)")
    fun deleteRangeByCredentialsId(dateFrom: LocalDate, dateTo: LocalDate, id: UUID)

    @Query("SELECT * FROM homeworks WHERE credentialsId = :id AND DATE(deadline) BETWEEN DATE(:dateFrom) AND DATE(:dateTo)")
    fun getRangeByCredentialsId(dateFrom: LocalDate, dateTo: LocalDate, id: UUID): Flow<List<Homeworks>>
}