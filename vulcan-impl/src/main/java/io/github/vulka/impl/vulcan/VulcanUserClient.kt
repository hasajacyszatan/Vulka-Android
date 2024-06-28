package io.github.vulka.impl.vulcan

import com.google.gson.Gson
import io.github.vulka.core.api.LoginCredentials
import io.github.vulka.core.api.UserClient
import io.github.vulka.core.api.types.Average
import io.github.vulka.core.api.types.EndGrade
import io.github.vulka.core.api.types.Grade
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.core.api.types.LessonChange
import io.github.vulka.core.api.types.LessonChangeType
import io.github.vulka.core.api.types.Parent
import io.github.vulka.core.api.types.Semester
import io.github.vulka.core.api.types.Student
import io.github.vulka.core.api.types.Teacher
import io.github.vulka.impl.vulcan.hebe.VulcanHebeApi
import io.github.vulka.impl.vulcan.hebe.types.HebeStudent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class VulcanUserClient(
    credentials: LoginCredentials
) : UserClient {
    val api = VulcanHebeApi()

    init {
        api.setup(credentials as VulcanLoginCredentials)
    }

    override suspend fun getStudents(): Array<Student> {
        val students = ArrayList<Student>()
        val response = api.getStudents()

        for (student in response) {
            val isParent = student.login.role == "Opiekun"
            students.add(
                Student(
                    fullName = "${student.pupil.firstName} ${student.pupil.lastName}",
                    classId = student.classDisplay,
                    isParent = isParent,
                    parent = if (isParent) Parent(
                        fullName = student.login.name
                    ) else null,
                    customData = Gson().toJson(student)
                )
            )
        }

        return students.toTypedArray()
    }

    override suspend fun getLuckyNumber(student: Student): Int {
        val hebeStudent = Gson().fromJson(student.customData, HebeStudent::class.java)
        return api.getLuckyNumber(hebeStudent, LocalDate.now())
    }

    override suspend fun getGrades(student: Student, semester: Semester): Array<Grade> {
        val hebeStudent = Gson().fromJson(student.customData, HebeStudent::class.java)
        val currentHebePeriod = hebeStudent.periods.find { it.current }!!

        val currentPeriod = hebeStudent.periods.find { it.number == semester.number && it.level == currentHebePeriod.level }!!

        val response = api.getGrades(hebeStudent,currentPeriod)

        val grades = java.util.ArrayList<Grade>()

        for (grade in response) {
            grades.add(
                Grade(
                    value = grade.content.replace("\\.0$".toRegex(), ""),
                    weight = grade.column.weight,
                    name = grade.column.name,
                    date = LocalDate.parse(grade.dateCreated.date),
                    subject = grade.column.subject.name,
                    teacher = Teacher(
                        fullName = grade.teacherCreated.displayName
                    )
                )
            )
        }

        return grades.toTypedArray()
    }

    override suspend fun getLessons(student: Student, dateFrom: LocalDate, dateTo: LocalDate): Array<Lesson> {
        val hebeStudent = Gson().fromJson(student.customData, HebeStudent::class.java)

        val lessonsResponse = api.getLessons(hebeStudent,dateFrom,dateTo)
        val changedLesson = api.getChangedLessons(hebeStudent,dateFrom,dateTo)

        val lessons = java.util.ArrayList<Lesson>()

        for (lesson in lessonsResponse) {
            // Skip lessons that is not for this student
            if (!lesson.visible)
                continue

            val change = changedLesson.find { it.scheduleId == lesson.id }

            lessons.add(
                Lesson(
                    subjectName = lesson.subject?.name ?: "",
                    startTime = lesson.time.from,
                    endTime = lesson.time.to,
                    classRoom = lesson.room?.code,
                    position = lesson.time.position,
                    change = if (change != null) {
                        LessonChange(
                            type = when (change.changes?.type) {
                                1 -> LessonChangeType.Canceled
                                2 -> LessonChangeType.Replacement
                                else -> LessonChangeType.Replacement
                            },
                            message = change.teacherAbsenceEffectName,
                            classRoom = change.room?.code,
                            newSubjectName = change.subject?.name,
                            newTeacher = change.teacherPrimary?.displayName?.let {
                                Teacher(
                                    fullName = it
                                )
                            }
                        )
                    } else null,
                    date = LocalDate.parse(lesson.date.date),
                    teacherName = lesson.teacher.displayName
                )
            )
        }

        return lessons.toTypedArray()
    }

    override suspend fun getSemesters(student: Student): Array<Semester> {
        val hebeStudent = Gson().fromJson(student.customData, HebeStudent::class.java)

        val currentSemester = hebeStudent.periods.find { it.current }!!
        val currentYearSemesters = hebeStudent.periods.filter { it.level == currentSemester.level }

        val semesters = java.util.ArrayList<Semester>()

        for (semester in currentYearSemesters) {
            semesters.add(
                Semester(
                    number = semester.number,
                    current = semester.current
                )
            )
        }
        return semesters.toTypedArray()
    }

    override suspend fun getEndGrades(student: Student, semester: Semester): Array<EndGrade> {
        val hebeStudent = Gson().fromJson(student.customData, HebeStudent::class.java)
        val currentHebePeriod = hebeStudent.periods.find { it.current }!!

        val currentPeriod = hebeStudent.periods.find { it.number == semester.number && it.level == currentHebePeriod.level }!!

        val response = api.getSummary(hebeStudent,currentPeriod)

        val grades = ArrayList<EndGrade>()

        for (grade in response) {
            grades.add(
                EndGrade(
                    proposedGrade = grade.entry1,
                    endGrade = grade.entry2,
                    subject = grade.subject.name
                )
            )
        }

        return grades.toTypedArray()
    }

    override suspend fun getSubjectAverages(student: Student, semester: Semester): Array<Average> {
        val hebeStudent = Gson().fromJson(student.customData, HebeStudent::class.java)
        val currentHebePeriod = hebeStudent.periods.find { it.current }!!

        val currentPeriod = hebeStudent.periods.find { it.number == semester.number && it.level == currentHebePeriod.level }!!

        val response = api.getAverage(hebeStudent,currentPeriod)

        val averages = ArrayList<Average>()

        for (average in response) {
            averages.add(
                Average(
                    average = average.average?.replace(",",".")?.toFloat(),
                    subject = average.subject.name
                )
            )
        }

        return averages.toTypedArray()
    }

    override fun shouldSyncSemesters(student: Student): Boolean {
        val hebeStudent = Gson().fromJson(student.customData, HebeStudent::class.java)

        val currentSemester = hebeStudent.periods.find { it.current }!!

        val date = LocalDateTime.now()
        return (date.toEpochSecond(ZoneOffset.UTC) * 1000 + date.nano / 1000000) > currentSemester.end.timestamp
    }
}