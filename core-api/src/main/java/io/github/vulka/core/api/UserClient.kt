package io.github.vulka.core.api

import io.github.vulka.core.api.types.Grade
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.core.api.types.Meeting
import io.github.vulka.core.api.types.Note
import io.github.vulka.core.api.types.Semester
import io.github.vulka.core.api.types.Student
import io.github.vulka.core.api.types.Summary
import java.time.LocalDate

interface UserClient {
    /**
     * Re-new login credentials if needed.
     */
    suspend fun renewCredentials() {
        // dummy
    }

    suspend fun getStudents(): Array<Student>
    suspend fun getLuckyNumber(student: Student): Int
    suspend fun getGrades(student: Student, semester: Semester): Array<Grade>
    suspend fun getLessons(
        student: Student,
        dateFrom: LocalDate = LocalDate.now(),
        dateTo: LocalDate = dateFrom
    ): Array<Lesson>
    suspend fun getSemesters(student: Student): Array<Semester>
    suspend fun getSummary(student: Student,semester: Semester): Array<Summary>
    suspend fun getNotes(student: Student): Array<Note>
    suspend fun getMeetings(student: Student): Array<Meeting>

    fun featuresSet(): Features

    /**
     * Return if semester should be sync e.g when new school year begins, used to prevent API rate limit in Vulcan
     */
    fun shouldSyncSemesters(student: Student): Boolean

    suspend fun getCurrentSemester(student: Student): Semester {
        return getSemesters(student).find { it.current }!!
    }
}
