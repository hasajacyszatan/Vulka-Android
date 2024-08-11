package io.github.vulka.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.vulka.database.entities.Credentials
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CredentialsDao : BaseDao<Credentials> {
    @Query("SELECT COUNT(*) FROM credentials")
    fun count(): Int

    @Query("SELECT * FROM credentials LIMIT 1")
    fun get(): Credentials?

    @Query("SELECT * FROM credentials WHERE id=:id LIMIT 1")
    fun getById(id: UUID): Flow<Credentials?>

    @Query("SELECT * FROM credentials")
    fun getAll(): List<Credentials>
}
