package io.github.vulka.core.api

import io.github.vulka.core.api.types.Grade
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.core.api.types.Student
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
    // TODO: add period selecting
    suspend fun getGrades(student: Student): Array<Grade>
    suspend fun getLessons(
        student: Student,
        dateFrom: LocalDate = LocalDate.now(),
        dateTo: LocalDate = dateFrom
    ): Array<Lesson>
}
