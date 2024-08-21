package io.github.vulka.impl.librus.api.timetable

import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.api.getSynergiaApi
import io.github.vulka.impl.librus.internal.api.types.LibrusIdField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

// When cookies expired:
// {
//	"Status": "Error",
//	"Message": "Request is denied"
//}

internal suspend fun LibrusUserClient.getTimetable(weekStart: LocalDate) =
    getSynergiaApi<TimetableResponse>("/Timetables?weekStart=$weekStart")

@Serializable
internal data class TimetableResponse(
    @SerialName("Timetable")
    val timetable: Map<String, Array<Array<TimetableDay>?>>
) {
    @Serializable
    data class TimetableDay(
        @SerialName("Lesson")
        val lesson: LibrusIdField,
        @SerialName("Classroom")
        val classroom: LibrusIdField,
        @SerialName("Subject")
        val subject: Subject,
        @SerialName("Teacher")
        val teacher: Teacher,
        @SerialName("DateFrom")
        val dateFrom: String,
        @SerialName("DateTo")
        val dateTo: String,
        @SerialName("HourFrom")
        val hourFrom: String,
        @SerialName("HourTo")
        val hourTo: String,
        @SerialName("LessonNo")
        val lessonNo: String,
        @SerialName("VirtualClassName")
        val virtualClassName: String?
    ) {
        @Serializable
        data class Subject(
//        @SerialName("Id")
//        val id: String,
            @SerialName("Name")
            val name: String
        )

        @Serializable
        data class Teacher(
            @SerialName("Id")
            val id: String,
            @SerialName("FirstName")
            val firstName: String,
            @SerialName("LastName")
            val lastName: String
        )
    }
}
