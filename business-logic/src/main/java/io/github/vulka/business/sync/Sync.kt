package io.github.vulka.business.sync

import android.content.Context
import io.github.vulka.business.crypto.decryptCredentials
import io.github.vulka.business.utils.getUserClient
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.UserClient
import io.github.vulka.core.api.types.Student
import io.github.vulka.database.Grades
import io.github.vulka.database.Homeworks
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
        repository.timetable.deleteRangeByCredentialsId(now.minusWeeks(2).toLocalDate(), now.plusWeeks(1).toLocalDate(), userId)

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
        if (client.featuresSet().isMeetingsSupported) {
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
    }

    val homeworksJob = coroutineScope.launch(handler) {
        if (client.featuresSet().isHomeworkSupported) {
            val now = LocalDateTime.of(2024,3,3,1,1,1)
            val homeworks = client.getHomework(student,now.toLocalDate(), now.plusWeeks(2).toLocalDate())
            repository.homeworks.deleteRangeByCredentialsId(now.toLocalDate(), now.plusWeeks(2).toLocalDate(), userId)

            for (homework in homeworks) {
                repository.homeworks.insert(
                    Homeworks(
                        homework = homework,
                        lastSync = now,
                        credentialsId = userId
                    )
                )
            }
        }
    }

    joinAll(luckyNumberJob, gradesJob, timetableJob, notesJob, meetingsJob, homeworksJob)

    if (error != null)
        throw error!!
}

fun getUserClientFromCredentials(platform: Platform, credentials: String): UserClient {
    val decryptedCredentials = decryptCredentials(credentials)
    val client = getUserClient(platform, decryptedCredentials)
    return client
}

suspend fun getStudentFromCredentials(context: Context, userId: UUID): Student {
    val repository = RoomModule.providesRepository(context)
    val dbCredentials = repository.credentials.getById(userId).first()!!
    return dbCredentials.student
}