package io.github.vulka.impl.vulcan.prometheus

import io.github.vulka.core.api.LoginCredentials
import io.github.vulka.core.api.UserClient
import io.github.vulka.core.api.types.*
import io.github.vulka.impl.vulcan.prometheus.hebece.HebeCeFeatures
import io.github.vulka.impl.vulcan.prometheus.hebece.VulcanHebeCeApi
import io.github.vulka.impl.vulcan.prometheus.hebece.types.HebeCeStudent
import io.github.vulka.impl.vulcan.toHebeCe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class VulcanPrometheusUserClient(
    credentials: LoginCredentials
) : UserClient {
    val api = VulcanHebeCeApi()

    init {
        api.setup(credentials as VulcanPrometheusLoginCredentials)
    }

    override suspend fun renewCredentials() {
        api.renewCredentials()
    }

    override suspend fun getStudents(): Array<Student> {
        val students = ArrayList<Student>()
        val response = api.getStudents()

        for (student in response) {
            val isParent = student.capabilities.contains("ROLE_CARETAKER")
            val parentName = if (student.messageBox.name.contains(" - R - ")) {
                student.messageBox.name.let {
                    it.substring(0, it.indexOf("-") - 1)
                }.split(" ").reversed().joinToString(" ")
            } else "Unknown parent name"
            students.add(
                Student(
                    fullName = "${student.pupil.firstName} ${student.pupil.lastName}",
                    classId = student.classDisplay,
                    isParent = isParent,
                    parent = if (isParent) Parent(
                        fullName = parentName
                    ) else null,
                    customData = Json.encodeToString<HebeCeStudent>(student)
                )
            )
        }

        return students.toTypedArray()
    }

    override suspend fun getLuckyNumber(student: Student): Int {
        return api.getLuckyNumber(student.toHebeCe(), LocalDate.now())
    }

    override suspend fun getGrades(student: Student, semester: Semester): Array<Grade> {
        val hebeStudent = student.toHebeCe()
        val currentHebePeriod = hebeStudent.periods.find { it.current }!!

        val currentPeriod = hebeStudent.periods.find { it.number == semester.number && it.level == currentHebePeriod.level }!!

        val response = api.getGrades(hebeStudent,currentPeriod)

        val grades = ArrayList<Grade>()

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
        val hebeStudent = student.toHebeCe()

        val lessonsResponse = api.getLessons(hebeStudent,dateFrom,dateTo)
        val changedLesson = api.getChangedLessons(hebeStudent,dateFrom,dateTo)

        val lessons = java.util.ArrayList<Lesson>()

        for (lesson in lessonsResponse) {
            // Skip lessons that is not for this student
            if (!lesson.visible)
                continue

            val change = changedLesson.find { it.substitution?.scheduleId == lesson.id }

            lessons.add(
                Lesson(
                    subjectName = lesson.subject?.name ?: lesson.event.orEmpty(),
                    startTime = lesson.time.from,
                    endTime = lesson.time.to,
                    classRoom = lesson.room?.code,
                    position = lesson.time.position,
                    change = if (change != null) {
                        LessonChange(
                            type = when (change.changes?.type) {
                                // Canceled at begin
                                1 -> LessonChangeType.Canceled
                                // Rescheduled
                                2 -> LessonChangeType.Replacement
                                // Canceled at end (Not sure)
                                3 -> LessonChangeType.Canceled
                                // Not sure
                                4 -> LessonChangeType.Canceled
                                // Fallback
                                else -> LessonChangeType.Replacement
                            },
                            message = change.substitution?.reason ?: change.substitution?.teacherAbsenceEffectName,
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
                    teacherName = lesson.teacher?.displayName
                )
            )
        }

        return lessons.toTypedArray()
    }

    override suspend fun getSemesters(student: Student): Array<Semester> {
        val hebeStudent = student.toHebeCe()

        val currentSemester = hebeStudent.periods.find { it.current }!!
        val currentYearSemesters = hebeStudent.periods.filter { it.level == currentSemester.level }

        val semesters = ArrayList<Semester>()

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

    override suspend fun getSummary(student: Student, semester: Semester): Array<Summary> {
        val hebeStudent = student.toHebeCe()
        val currentHebePeriod = hebeStudent.periods.find { it.current }!!

        val currentPeriod = hebeStudent.periods.find { it.number == semester.number && it.level == currentHebePeriod.level }!!

        val endGradesResponse = api.getSummaryGrades(hebeStudent,currentPeriod)
        // TODO: support for year average
        val averagesResponse = api.getAverages(hebeStudent,currentPeriod).filter { it.scope == "periodic" }

        val summaryMap = mutableMapOf<String, Summary>()

        endGradesResponse.forEach { endGrade ->
            val subjectName = endGrade.subject.name
            summaryMap[subjectName] = Summary(
                proposedGrade = getGradeNumberFromShortcut(endGrade.entry1),
                endGrade = getGradeNumberFromShortcut(endGrade.entry2),
                average = null,
                subject = subjectName
            )
        }

        averagesResponse.forEach { averageGrade ->
            val subjectName = averageGrade.subject.name
            val averageValue = averageGrade.average?.replace(",",".")?.toFloatOrNull()

            if (summaryMap.containsKey(subjectName)) {
                val existingSummary = summaryMap[subjectName]!!
                summaryMap[subjectName] = existingSummary.copy(average = averageValue)
            } else {
                summaryMap[subjectName] = Summary(
                    proposedGrade = null,
                    endGrade = null,
                    average = averageValue,
                    subject = subjectName
                )
            }
        }

        return summaryMap.values.toTypedArray()
    }

    override suspend fun getNotes(student: Student): Array<Note> {
        val hebeStudent = student.toHebeCe()

        val notes = ArrayList<Note>()
        val response = api.getNotes(hebeStudent)

        for (note in response) {
            notes.add(
                Note(
                    name = note.category?.name,
                    content = note.content,
                    points = note.points?.toString(),
                    creator = note.creator.displayName,
                    date = LocalDate.parse(note.dateValid.date)
                )
            )
        }

        val (startDate,endDate) = getSchoolYearDates(hebeStudent)
        return notes.filter { it.date.isBefore(endDate) && it.date.isAfter(startDate) }.toTypedArray()
    }

    override suspend fun getMeetings(student: Student): Array<Meeting> {
        val hebeStudent = student.toHebeCe()
        val (startDate,_) = getSchoolYearDates(hebeStudent)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val meetings = ArrayList<Meeting>()
        val response = api.getMeetings(hebeStudent,startDate)

        for (meeting in response) {
            meetings.add(
                Meeting(
                    topic = meeting.why,
                    dateTime = LocalDateTime.parse("${meeting.`when`.date} ${meeting.`when`.time}",formatter),
                    place = meeting.where,
                    agenda = meeting.agenda
                )
            )
        }

        return meetings.toTypedArray()
    }

    override suspend fun getHomework(student: Student, dateFrom: LocalDate, dateTo: LocalDate): Array<Homework> {
        val hebeStudent = student.toHebeCe()

        val homeworks = ArrayList<Homework>()
        val response = api.getHomeworks(hebeStudent,dateFrom, dateTo)

        for (homework in response) {
            homeworks.add(
                Homework(
                    content = homework.content,
                    dateCreated = LocalDate.parse(homework.dateCreated.date),
                    deadline = LocalDate.parse(homework.deadline.date),
                    creator = homework.creator.displayName,
                    subject = homework.subject.name,
                    attachments = homework.attachments.map {
                        HomeworkAttachment(
                            name = it.name,
                            data = it.link
                        )
                    },
                    isAnswerRequired = homework.isAnswerRequired
                )
            )
        }

        return homeworks.toTypedArray()
    }

    override suspend fun getExam(student: Student, dateFrom: LocalDate, dateTo: LocalDate): Array<Exam> {
        val exams = ArrayList<Exam>()
        val response = api.getExams(student.toHebeCe(), dateFrom, dateTo)

        for (exam in response) {
            exams.add(
                Exam(
                    type = exam.type,
                    content = exam.content,
                    dateCreated = LocalDate.parse(exam.dateCreated.date),
                    deadline = LocalDate.parse(exam.deadline.date),
                    creator = exam.creator.displayName,
                    subject = exam.subject.name
                )
            )
        }

        return exams.toTypedArray()
    }

    override suspend fun getAttendance(student: Student, dateFrom: LocalDate, dateTo: LocalDate): Array<Attendance> {
        val attendances = ArrayList<Attendance>()
        val response = api.getAttendance(student.toHebeCe(),dateFrom,dateTo).filter { it.visible }

        for (attendance in response) {
            attendances.add(
                Attendance(
                    subject = attendance.subject.name,
                    date = LocalDate.parse(attendance.day.date),
                    time = LocalTime.parse(attendance.timeSlot.from),
                    teacher = attendance.teacherPrimary.displayName,
                    presenceType = when (attendance.presenceType.categoryId) {
                        1 -> PresenceType.Presence
                        2 -> PresenceType.Absence
                        3 -> PresenceType.AbsenceExcused
                        4 -> PresenceType.Lateness
                        5 -> PresenceType.LatenessExcused
                        6 -> PresenceType.AbsenceForSchoolReasons
                        7 -> PresenceType.Exemption
                        else -> PresenceType.Unknown
                    },
                    justificationStatus = when (attendance.justificationStatus) {
                        0 -> JustificationStatus.Requested
                        1 -> JustificationStatus.Accepted
                        2 -> JustificationStatus.Rejected
                        else -> null
                    },
                    topic = attendance.topic,
                    position = attendance.subject.position
                )
            )
        }

        return attendances.toTypedArray()
    }

    private fun getSchoolYearDates(hebeStudent: HebeCeStudent): Pair<LocalDate,LocalDate> {
        // Filter by this school year
        val startDate = LocalDate.parse(hebeStudent.journal.yearStart.date)
        val endDate = LocalDate.parse(hebeStudent.journal.yearEnd.date)
        return startDate to endDate
    }

    private fun getGradeNumberFromShortcut(shortcut: String?): String? {
        return when (shortcut) {
            "cel" -> "6"
            "bdb" -> "5"
            "db" -> "4"
            "dst" -> "3"
            "dop" -> "2"
            "ndst" -> "1"
            else -> shortcut
        }
    }

    override fun shouldSyncSemesters(student: Student): Boolean {
        val hebeStudent = student.toHebeCe()

        val currentSemester = hebeStudent.periods.find { it.current }!!

        val date = LocalDateTime.now()
        return (date.toEpochSecond(ZoneOffset.UTC) * 1000 + date.nano / 1000000) > currentSemester.end.timestamp
    }

    override fun featuresSet() = HebeCeFeatures()
}