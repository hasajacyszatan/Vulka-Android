package io.github.vulka.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    version = 1,
    entities = [
        Credentials::class,
        LuckyNumber::class,
        Grades::class,
        Timetable::class,
        Semesters::class
    ],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VulkaDatabase : RoomDatabase() {
    abstract fun credentialsDao(): CredentialsDao
    abstract fun luckyNumberDao(): LuckyNumberDao
    abstract fun gradesDao(): GradesDao
    abstract fun timetableDao(): TimetableDao
    abstract fun semestersDao(): SemestersDao
}

/**
 * Database provider singleton class.
 */
object DatabaseProvider {
    private var database: VulkaDatabase? = null

    /**
     * Get database instance. If the database is not initialized, it will be initialize.
     */
    fun getInstance(context: Context): VulkaDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                context,
                VulkaDatabase::class.java,
                "vulka.db"
            )
                .allowMainThreadQueries()
                .build()
        }

        return database as VulkaDatabase
    }
}
