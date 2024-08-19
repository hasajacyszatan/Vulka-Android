package io.github.vulka.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.vulka.database.entities.Grades
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

@Dao
interface GradesDao : BaseDao<Grades> {
    @Query("DELETE FROM grades WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM grades WHERE credentialsId = :id AND semester = :semester")
    fun getBySemesterAndCredentialsId(semester: Int, id: UUID): Flow<List<Grades>>

    @Query("SELECT COUNT(*) FROM grades WHERE subject = :subjectName AND credentialsId = :id AND semester = :semester")
    fun countBySubjectSemesterAndCredentials(id: UUID, semester: Int, subjectName: String): Flow<Int>

    @Query("SELECT * FROM Grades WHERE DATE(date) BETWEEN DATE(:weekAgoDate) AND DATE('now') AND credentialsId = :id")
    fun getFromLastWeek(id: UUID, weekAgoDate: LocalDate): Flow<List<Grades>>
}
