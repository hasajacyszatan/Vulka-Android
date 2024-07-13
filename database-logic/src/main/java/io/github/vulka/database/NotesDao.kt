package io.github.vulka.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notes: Notes)

    @Update
    suspend fun update(notes: Notes)

    @Query("DELETE FROM notes WHERE credentialsId = :id")
    fun deleteByCredentialsId(id: UUID)

    @Query("SELECT * FROM notes WHERE credentialsId = :credentialsId LIMIT 1")
    fun get(credentialsId: UUID): Flow<Notes>

    @Query("SELECT * FROM notes WHERE credentialsId = :credentialsId")
    fun getByCredentialsId(credentialsId: UUID): Flow<List<Notes>>
}