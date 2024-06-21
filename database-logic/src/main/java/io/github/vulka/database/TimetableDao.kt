package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate
import java.util.UUID

@Dao
interface TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timetable: Timetable)

    @Update
    suspend fun update(timetable: Timetable)

    @Delete
    fun delete(timetable: Timetable)

    @Query("DELETE FROM timetable WHERE credentialsId = :id AND DATE(lessonDate) >= DATE(:dateFrom) AND DATE(lessonDate) <= DATE(:dateTo)")
    fun deleteRangeByCredentialsId(dateFrom: LocalDate, dateTo: LocalDate,id: UUID)

    @Query("DELETE FROM timetable WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM timetable WHERE credentialsId = :id AND lessonDate = :date")
    fun getByDateAndCredentialsId(id: UUID,date: LocalDate): List<Timetable>
}