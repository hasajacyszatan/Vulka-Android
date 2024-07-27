package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Query
import io.github.vulka.core.api.types.Homework
import java.time.LocalDate
import java.util.UUID

@Dao
interface HomeworksDao : BaseDao<Homeworks> {
    @Query("DELETE FROM homeworks WHERE credentialsId = :id AND DATE(deadline) BETWEEN DATE(:dateFrom) AND DATE(:dateTo)")
    fun deleteRangeByCredentialsId(dateFrom: LocalDate, dateTo: LocalDate, id: UUID)
}