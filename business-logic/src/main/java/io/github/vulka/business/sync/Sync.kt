package io.github.vulka.business.sync

import android.content.Context
import io.github.vulka.business.crypto.decryptCredentials
import io.github.vulka.business.utils.getUserClient
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.UserClient
import io.github.vulka.core.api.types.Student
import io.github.vulka.database.Grades
import io.github.vulka.database.LuckyNumber
import io.github.vulka.database.Meetings
import io.github.vulka.database.Notes
import io.github.vulka.database.Semesters
import io.github.vulka.database.Timetable
import io.github.vulka.database.injection.RoomModule
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

suspend fun sync(
    context: Context,
    platform: Platform,
    userId: UUID,
    credentials: String,
    student: Student
) {
    val client = getUserClient(platform, credentials)

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    // re-new login credentials if needed
    client.renewCredentials()

    val repository = RoomModule.providesRepository(context)

    var error: Throwable? = null

    val handler = CoroutineExceptionHandler { _, exception ->
        error = exception
    }

    val luckyNumberJob = coroutineScope.launch(handler) {
        // sync lucky number
        val newLuckyNumber = client.getLuckyNumber(student)
        val luckyNumber = repository.luckyNumber.get(userId).first()
        if (luckyNumber != null && luckyNumber.number != newLuckyNumber) {
            repository.luckyNumber.update(
                luckyNumber.copy(number = newLuckyNumber)
            )
        } else {
            repository.luckyNumber.insert(
                LuckyNumber(
                    credentialsId = userId,
                    number = newLuckyNumber
                )
            )
        }
    }

    val gradesJob = coroutineScope.launch(handler) {
        // TODO: Add use of shouldSyncSemesters
        // sync grades and semesters
        val semesters = client.getSemesters(student)
        repository.grades.deleteByCredentialsId(userId)
        repository.semesters.deleteByCredentialsId(userId)

        for (semester in semesters) {
            repository.semesters.insert(
                Semesters(
                    semester = semester,
                    credentialsId = userId
                )
            )

            // sync grades
            val grades = client.getGrades(student, semester)
            for (grade in grades) {
                repository.grades.insert(
                    Grades(
                        grade = grade,
                        semester = semester.number,
                        credentialsId = userId
                    )
                )
            }
        }
    }

    val timetableJob = coroutineScope.launch(handler) {
        // Sync timetable
        val now = LocalDateTime.now()
        val lessons = client.getLessons(student, now.minusWeeks(2).toLocalDate(), now.plusWeeks(1).toLocalDate())
        repository.timetable.deleteByCredentialsId(userId)

        for (lesson in lessons) {
            repository.timetable.insert(
                Timetable(
                    lesson = lesson,
                    lastSync = now,
                    credentialsId = userId
                )
            )
        }
    }

    val notesJob = coroutineScope.launch(handler) {
        val notes = client.getNotes(student)
        repository.notes.deleteByCredentialsId(userId)

        for (note in notes) {
            repository.notes.insert(
                Notes(
                    note = note,
                    credentialsId = userId
                )
            )
        }
    }

    val meetingsJob = coroutineScope.launch(handler) {
        val meetings = client.getMeetings(student)
        repository.meetings.deleteByCredentialsId(userId)

        for (meeting in meetings) {
            repository.meetings.insert(
                Meetings(
                    meeting = meeting,
                    credentialsId = userId
                )
            )
        }
    }

    joinAll(luckyNumberJob, gradesJob, timetableJob, notesJob, meetingsJob)

    if (error != null)
        throw error!!
}

fun getUserClientFromCredentials(platform: Platform, credentials: String): UserClient {
    val decryptedCredentials = decryptCredentials(credentials)
    val client = getUserClient(platform, decryptedCredentials)
    return client
}

// TODO: remove runBlocking
fun getStudentFromCredentials(context: Context, userId: UUID): Student = runBlocking {
    val repository = RoomModule.providesRepository(context)
    val dbCredentials = repository.credentials.getById(userId).first()!!
    return@runBlocking dbCredentials.student
}

fun syncTimetableAtSwitch(context: Context, client: UserClient, student: Student, selectedDate: LocalDate, userId: UUID) = runBlocking {
    val now = LocalDateTime.now()

    val repository = RoomModule.providesRepository(context)
    val lessons = client.getLessons(student,selectedDate)
    repository.timetable.deleteRangeByCredentialsId(selectedDate,selectedDate,userId)
    for (lesson in lessons) {
        repository.timetable.insert(
            Timetable(
                lesson = lesson,
                lastSync = now,
                credentialsId = userId
            )
        )
    }
}
// TODO: remove runBlocking
fun checkIfTimetableShouldBeSync(context: Context, selectedDateTime: LocalDate, userId: UUID): Boolean = runBlocking {
    val repository = RoomModule.providesRepository(context)

    val lessons = repository.timetable.getByDateAndCredentialsId(userId,selectedDateTime).first()

    if (lessons.isNotEmpty()) {
        val lesson = lessons[0]

        val duration = Duration.between(lesson.lastSync, LocalDateTime.now())
        return@runBlocking duration.toMinutes() >= 15
    } else
        return@runBlocking true
}