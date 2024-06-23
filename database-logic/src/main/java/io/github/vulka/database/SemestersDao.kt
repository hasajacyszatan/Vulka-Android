package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.UUID

@Dao
interface SemestersDao {
    @Insert
    suspend fun insert(semesters: Semesters)

    @Update
    suspend fun update(semesters: Semesters)

    @Delete
    fun delete(semesters: Semesters)

    @Query("DELETE FROM semesters WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM semesters WHERE credentialsId = :id")
    fun getByCredentialsId(id: UUID): List<Semesters>
}