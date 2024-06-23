package io.github.vulka.impl.librus

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.github.vulka.core.api.UserClient
import io.github.vulka.core.api.types.Grade
import io.github.vulka.core.api.types.Lesson
import io.github.vulka.core.api.types.Parent
import io.github.vulka.core.api.types.Semester
import io.github.vulka.core.api.types.Student
import io.github.vulka.core.api.types.StudentImpl
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
import io.github.vulka.impl.librus.internal.api.types.TimetablesDay
import io.github.vulka.impl.librus.internal.api.types.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.cookies.ConstantCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.renderCookieHeader
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
        val classInfo = internalRequestClass(me.class_.id)

        return arrayOf(
            Student(
                // maybe "user" for parent account and "account" for student account
                fullName = "${me.user.firstName} ${me.user.lastName}",
                isParent = userProfile.accountType == UserProfile.AccountType.PARENT,
                parent = Parent(
                    name = "${me.account.firstName} ${me.account.lastName}"
                ),
                classId = "${classInfo.number}${classInfo.symbol}",
                impl = StudentImpl()
            )
        )
    }

    override suspend fun getLuckyNumber(student: Student): Int {
        val response = internalRequestLuckyNumber()
        return response.luckyNumber
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
                    name = category.name,
                    date = LocalDate.parse(grade.date),
                    subjectName = subject.name,
                    subjectCode = subject.short,
                    teacherName = "${teacher?.firstName} ${teacher?.lastName}"
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

        fun getTimetableDay(date: LocalDate): Array<Array<TimetablesDay>?>? {
            println(date.toString())

            return try {
                val type = object : TypeToken<Array<Array<TimetablesDay>?>?>() {}.type
                return Gson().fromJson(timetable[date.toString()], type)
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
                            room = classRooms.find { x -> x.id.toString() == lesson.classroom.id }?.name,
                            groupName = lesson.virtualClassName ?: null,
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

    override fun shouldSyncSemesters(student: Student): Boolean {
        // Stub, but maybe in Librus should stay always true
        return true
    }
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
