package io.github.vulka.impl.librus

import io.github.vulka.core.api.UserClient
import io.github.vulka.core.api.types.Attendance
import io.github.vulka.core.api.types.Exam
import io.github.vulka.core.api.types.Grade
import io.github.vulka.core.api.types.Homework
import io.github.vulka.core.api.types.HomeworkAttachment
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.core.api.types.Meeting
import io.github.vulka.core.api.types.Note
import io.github.vulka.core.api.types.Parent
import io.github.vulka.core.api.types.Semester
import io.github.vulka.core.api.types.Student
import io.github.vulka.core.api.types.Summary
import io.github.vulka.core.api.types.Teacher
import io.github.vulka.impl.librus.internal.api.LibrusUserProfileResponse
import io.github.vulka.impl.librus.internal.api.internalRequestClass
import io.github.vulka.impl.librus.internal.api.internalRequestClassrooms
import io.github.vulka.impl.librus.internal.api.internalRequestGrades
import io.github.vulka.impl.librus.internal.api.internalRequestGradesCategories
import io.github.vulka.impl.librus.internal.api.internalRequestLuckyNumber
import io.github.vulka.impl.librus.internal.api.internalRequestMe
import io.github.vulka.impl.librus.internal.api.internalRequestSubjects
import io.github.vulka.impl.librus.internal.api.internalRequestTimetable
import io.github.vulka.impl.librus.internal.api.internalRequestUserProfile
import io.github.vulka.impl.librus.internal.api.internalRequestUsers
import io.github.vulka.impl.librus.internal.api.types.TimetableDay
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.cookies.ConstantCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.renderCookieHeader
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import java.time.DayOfWeek
import java.time.LocalDate

class LibrusUserClient(
    internal var credentials: LibrusLoginCredentials
) : UserClient {
    internal lateinit var client: HttpClient

    internal fun initClient(cookies: List<Cookie>) {
        client = HttpClient(OkHttp) {
            install(HttpCookies) {
                storage = ConstantCookiesStorage(*cookies.toTypedArray())
            }

            BrowserUserAgent()
        }
    }

    override suspend fun renewCredentials() {
        val loginData = credentials.request
        credentials = LibrusLoginClient().login(loginData) as LibrusLoginCredentials
        initClient(credentials.cookies)
    }

    override suspend fun getStudents(): Array<Student> {
        val userProfile = internalRequestUserProfile()
        val me = internalRequestMe()
        val classInfo = internalRequestClass(me.`class`.id)

        return arrayOf(
            Student(
                // maybe "user" for parent account and "account" for student account
                fullName = "${me.user.firstName} ${me.user.lastName}",
                isParent = userProfile.accountType == LibrusUserProfileResponse.AccountType.Parent,
                parent = Parent(
                    fullName = "${me.account.firstName} ${me.account.lastName}"
                ),
                classId = "${classInfo.number}${classInfo.symbol}"
            )
        )
    }

    override suspend fun getLuckyNumber(student: Student): Int {
        val response = internalRequestLuckyNumber()
        return response.number ?: 0
    }

    override suspend fun getGrades(student: Student, semester: Semester): Array<Grade> {
        val grades = internalRequestGrades()
        val categories = internalRequestGradesCategories()
        val subjects = internalRequestSubjects()
        val teachers = internalRequestUsers()

        val gradesList = ArrayList<Grade>()

        for (grade in grades) {
            val teacher = teachers.find { it.id == grade.addedBy.id }
            val subject = subjects.find { it.id == grade.subject.id }!!
            val category = categories.find { it.id == grade.category.id }!!

            gradesList.add(
                Grade(
                    value = grade.grade,
                    weight = category.weight ?: 0f,
                    name = category.name ?: "",
                    date = LocalDate.parse(grade.date),
                    subject = subject.name,
                    teacher = Teacher(
                        fullName = "${teacher?.firstName} ${teacher?.lastName}"
                    )
                )
            )
        }

        return gradesList.toTypedArray()
    }

    override suspend fun getLessons(
        student: Student,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): Array<Lesson> {
        val lessons = ArrayList<Lesson>()

        val classRooms = internalRequestClassrooms()

        lateinit var timetable: JsonObject

        fun getTimetableDay(date: LocalDate): Array<Array<TimetableDay>?>? {
            println(date.toString())

            return try {
                return Json.decodeFromJsonElement(timetable[date.toString()]!!)
            } catch (e: Exception) {
                null
            }
        }

        var currentDate = dateFrom
        while (!currentDate.isAfter(dateTo)) {
            if (currentDate.dayOfWeek == DayOfWeek.MONDAY) {
                timetable = internalRequestTimetable(currentDate.toString())
            }

            if (currentDate.isAfter(dateTo))
                break // Stop adding lessons beyond dateTo

            getTimetableDay(currentDate)?.forEach {
                if (!it.isNullOrEmpty()) {
                    val lesson = it[0]

                    lessons.add(
                        Lesson(
                            subjectName = lesson.subject.name,
                            position = lesson.lessonNo.toInt(),
                            teacherName = "${lesson.teacher.firstName} ${lesson.teacher.lastName}",
                            classRoom = classRooms.find { x -> x.id == lesson.classroom.id }?.name,
                            groupName = lesson.virtualClassName,
                            date = currentDate,
                            startTime = lesson.hourFrom,
                            endTime = lesson.hourTo
                            // TODO: changes
//                            change = when (i) {
//                                1 -> LessonChange(
//                                    type = LessonChangeType.Canceled,
//                                    message = "Stub reason",
//                                    room = "101",
//                                    newSubjectName = "New subject stub",
//                                    newTeacherName = "New teacher stub"
//                                )
//
//                                4 -> LessonChange(
//                                    type = LessonChangeType.Replacement,
//                                    message = "Stub reason",
//                                    room = "102",
//                                    newSubjectName = "New subject stub",
//                                    newTeacherName = "New teacher stub"
//                                )
//
//                                else -> null
//                            }
                        )
                    )
                }
            }

            currentDate = currentDate.plusDays(1)
        }

        return lessons.toTypedArray()
    }

    override suspend fun getSemesters(student: Student): Array<Semester> {
        // Stub
        val semesters = ArrayList<Semester>()
        for (i in 1..2) {
            semesters.add(
                Semester(
                    number = i,
                    current = i == 2
                )
            )
        }

        return semesters.toTypedArray()
    }

    override suspend fun getSummary(student: Student, semester: Semester): Array<Summary> {
        // Stub
        val summary = ArrayList<Summary>()
        summary.add(
            Summary(
                proposedGrade = "4",
                endGrade = "5",
                average = 4.80f,
                subject = "Stub subject"
            )
        )
        return summary.toTypedArray()
    }

    override suspend fun getNotes(student: Student): Array<Note> {
        // Stub
        val notes = ArrayList<Note>()
        for (i in 0..5) {
            notes.add(
                Note(
                    name = if (i % 2 == 0) "Negative note" else "Positive note",
                    content = "Some stub content",
                    points = null,
                    creator = "Stub creator",
                    date = LocalDate.of(2024,3,7)
                )
            )
        }
        return notes.toTypedArray()
    }

    override suspend fun getMeetings(student: Student): Array<Meeting> {
        // Not available on this platform
        return emptyArray()
    }

    override suspend fun getHomework(
        student: Student,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): Array<Homework> {
        // Stub
        val homeworks = ArrayList<Homework>()
        val date = LocalDate.now()
        for (i in 0..5) {
            homeworks.add(
                Homework(
                    content = "Stub homework $i",
                    dateCreated = date,
                    deadline = date.plusWeeks(1),
                    creator = "Stub creator",
                    subject = "Stub subject",
                    attachments = if (i % 2 == 0) listOf(
                        HomeworkAttachment(
                            name = "Stub attachment",
                            data = "https://exmaple.com"
                        )
                    ) else emptyList(),
                    isAnswerRequired = false
                )
            )
            date.plusDays(1)
        }
        return homeworks.toTypedArray()
    }

    override suspend fun getExam(
        student: Student,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): Array<Exam> {
        // Stub
        val exams = ArrayList<Exam>()
        val date = LocalDate.now()
        for (i in 0..5) {
            exams.add(
                Exam(
                    type = "Exam",
                    content = "Stub exam $i",
                    dateCreated = date,
                    deadline = date.plusWeeks(1),
                    creator = "Stub creator",
                    subject = "Stub subject",
                )
            )
            date.plusDays(1)
        }
        return exams.toTypedArray()
    }

    override suspend fun getAttendance(
        student: Student,
        dateFrom: LocalDate,
        dateTo: LocalDate
    ): Array<Attendance> {
        return emptyArray()
    }

    override fun shouldSyncSemesters(student: Student): Boolean {
        // Stub, but maybe in Librus should stay always true
        return true
    }

    override fun featuresSet() = LibrusFeatures()
}

fun HttpRequestBuilder.applyCookie(cookie: Cookie) = cookie.run {
    val renderedCookie = cookie.let(::renderCookieHeader)
    if (HttpHeaders.Cookie !in headers) {
        headers.append(HttpHeaders.Cookie, renderedCookie)
        return
    }
    // Client cookies are stored in a single header "Cookies" and multiple values are separated with ";"
    headers[HttpHeaders.Cookie] = headers[HttpHeaders.Cookie] + "; " + renderedCookie
}
