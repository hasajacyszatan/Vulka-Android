package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

@Dao
interface GradesDao : BaseDao<Grades> {
    @Query("DELETE FROM grades WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM grades WHERE credentialsId = :id AND semester = :semester")
    fun getBySemesterAndCredentialsId(semester: Int, id: UUID): List<Grades>

    @Query("SELECT COUNT(*) FROM grades WHERE subject = :subjectName AND credentialsId = :id AND semester = :semester")
    fun countBySubjectSemesterAndCredentials(id: UUID, semester: Int, subjectName: String): Int

    @Query("SELECT * FROM Grades WHERE DATE(date) >= DATE(:weekAgoDate) AND credentialsId = :id")
    fun getFromLastWeek(id: UUID, weekAgoDate: LocalDate): Flow<List<Grades>>
}
