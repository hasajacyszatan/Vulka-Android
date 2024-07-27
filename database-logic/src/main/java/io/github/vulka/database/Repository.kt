package io.github.vulka.database

import android.content.Context

interface RepositoryInterface {
    val credentials: CredentialsDao
    val luckyNumber: LuckyNumberDao
    val grades: GradesDao
    val timetable: TimetableDao
    val semesters: SemestersDao
    val notes: NotesDao
    val meetings: MeetingsDao
    val homeworks: HomeworksDao
}

class Repository(context: Context) : RepositoryInterface {
    private val database = DatabaseProvider.getInstance(context)

    override val credentials = database.credentialsDao()
    override val luckyNumber = database.luckyNumberDao()
    override val grades = database.gradesDao()
    override val timetable = database.timetableDao()
    override val semesters = database.semestersDao()
    override val notes = database.notesDao()
    override val meetings = database.meetingsDao()
    override val homeworks = database.homeworksDao()
}
