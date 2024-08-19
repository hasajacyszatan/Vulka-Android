package io.github.vulka.impl.vulcan.prometheus

import io.github.vulka.core.api.LoginCredentials
import io.github.vulka.core.api.UserClient
import io.github.vulka.core.api.types.Exam
import io.github.vulka.core.api.types.Grade
import io.github.vulka.core.api.types.Homework
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.core.api.types.Meeting
import io.github.vulka.core.api.types.Note
import io.github.vulka.core.api.types.Semester
import io.github.vulka.core.api.types.Student
import io.github.vulka.core.api.types.Summary
import io.github.vulka.impl.vulcan.hebe.HebeFeatures
import java.time.LocalDate

class VulcanPrometheusUserClient(
    credentials: LoginCredentials
) : UserClient {
    override suspend fun getStudents(): Array<Student> {
        TODO("Not yet implemented")
    }

    override suspend fun getLuckyNumber(student: Student): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getGrades(student: Student, semester: Semester): Array<Grade> {
        TODO("Not yet implemented")
    }

    override suspend fun getLessons(
        student: Student,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): Array<Lesson> {
        TODO("Not yet implemented")
    }

    override suspend fun getSemesters(student: Student): Array<Semester> {
        TODO("Not yet implemented")
    }

    override suspend fun getSummary(student: Student, semester: Semester): Array<Summary> {
        TODO("Not yet implemented")
    }

    override suspend fun getNotes(student: Student): Array<Note> {
        TODO("Not yet implemented")
    }

    override suspend fun getMeetings(student: Student): Array<Meeting> {
        TODO("Not yet implemented")
    }

    override suspend fun getHomework(
        student: Student,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): Array<Homework> {
        TODO("Not yet implemented")
    }

    override suspend fun getExam(
        student: Student,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): Array<Exam> {
        TODO("Not yet implemented")
    }

    override fun featuresSet() = HebeFeatures()

    override fun shouldSyncSemesters(student: Student): Boolean {
        TODO("Not yet implemented")
    }
}