package io.github.vulka.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.vulka.database.entities.Timetable
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

@Dao
interface TimetableDao : BaseDao<Timetable> {
    @Query("DELETE FROM timetable WHERE credentialsId = :id AND DATE(date) BETWEEN DATE(:dateFrom) AND DATE(:dateTo)")
    fun deleteRangeByCredentialsId(dateFrom: LocalDate, dateTo: LocalDate, id: UUID)

    @Query("DELETE FROM timetable WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM timetable WHERE credentialsId = :id AND DATE(date) = DATE(:date)")
    fun getByDateAndCredentialsId(id: UUID, date: LocalDate): Flow<List<Timetable>>
}
