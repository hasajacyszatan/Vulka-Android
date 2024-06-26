package io.github.vulka.business.sync

import android.content.Context
import io.github.vulka.business.crypto.decryptCredentials
import io.github.vulka.business.utils.getUserClient
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.UserClient
import io.github.vulka.core.api.types.Student
import io.github.vulka.database.Grades
import io.github.vulka.database.LuckyNumber
import io.github.vulka.database.Semesters
import io.github.vulka.database.Timetable
import io.github.vulka.database.injection.RoomModule
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

    // re-new login credentials if needed
    client.renewCredentials()

    val repository = RoomModule.providesRepository(context)

    // sync lucky number
    val newLuckyNumber = client.getLuckyNumber(student)
    val luckyNumber = repository.luckyNumber.get(userId)
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

fun getUserClientFromCredentials(platform: Platform, credentials: String): UserClient {
    val decryptedCredentials = decryptCredentials(credentials)
    val client = getUserClient(platform, decryptedCredentials)
    return client
}

fun getStudentFromCredentials(context: Context, userId: UUID): Student {
    val repository = RoomModule.providesRepository(context)
    val dbCredentials = repository.credentials.getById(userId)!!
    return dbCredentials.student
}

suspend fun syncTimetableAtSwitch(context: Context, client: UserClient, student: Student, selectedDate: LocalDate, userId: UUID) {
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

fun checkIfTimetableShouldBeSync(context: Context,selectedDateTime: LocalDate,userId: UUID): Boolean {
    val repository = RoomModule.providesRepository(context)

    val lessons = repository.timetable.getByDateAndCredentialsId(userId,selectedDateTime)

    if (lessons.isNotEmpty()) {
        val lesson = lessons[0]

        val duration = Duration.between(lesson.lastSync, LocalDateTime.now())
        return duration.toMinutes() >= 15
    } else
        return true
}