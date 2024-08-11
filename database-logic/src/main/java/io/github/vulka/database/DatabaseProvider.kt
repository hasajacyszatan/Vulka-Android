package io.github.vulka.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.vulka.database.dao.CredentialsDao
import io.github.vulka.database.dao.ExamsDao
import io.github.vulka.database.dao.GradesDao
import io.github.vulka.database.dao.HomeworksDao
import io.github.vulka.database.dao.LuckyNumberDao
import io.github.vulka.database.dao.MeetingsDao
import io.github.vulka.database.dao.NotesDao
import io.github.vulka.database.dao.SemestersDao
import io.github.vulka.database.dao.SummaryDao
import io.github.vulka.database.dao.TimetableDao
import io.github.vulka.database.entities.Credentials
import io.github.vulka.database.entities.Exams
import io.github.vulka.database.entities.Grades
import io.github.vulka.database.entities.Homeworks
import io.github.vulka.database.entities.LuckyNumber
import io.github.vulka.database.entities.Meetings
import io.github.vulka.database.entities.Notes
import io.github.vulka.database.entities.Semesters
import io.github.vulka.database.entities.Summary
import io.github.vulka.database.entities.Timetable

@Database(
    version = 1,
    entities = [
        Credentials::class,
        LuckyNumber::class,
        Grades::class,
        Timetable::class,
        Semesters::class,
        Notes::class,
        Meetings::class,
        Homeworks::class,
        Exams::class,
        Summary::class
    ]
)
@TypeConverters(Converters::class)
abstract class VulkaDatabase : RoomDatabase() {
    abstract fun credentialsDao(): CredentialsDao
    abstract fun luckyNumberDao(): LuckyNumberDao
    abstract fun gradesDao(): GradesDao
    abstract fun timetableDao(): TimetableDao
    abstract fun semestersDao(): SemestersDao
    abstract fun notesDao(): NotesDao
    abstract fun meetingsDao(): MeetingsDao
    abstract fun homeworksDao(): HomeworksDao
    abstract fun examsDao(): ExamsDao
    abstract fun summaryDao(): SummaryDao
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