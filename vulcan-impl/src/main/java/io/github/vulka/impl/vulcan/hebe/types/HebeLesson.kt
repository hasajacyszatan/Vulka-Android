package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeLesson(
    @SerialName("Id")
    val id: Int,
    @SerialName("Date")
    val date: HebeDate,
    @SerialName("TimeSlot")
    val time: HebeTimeSlot,
    @SerialName("Room")
    val room: HebeLessonRoom? = null,
    @SerialName("TeacherPrimary")
    val teacher: HebeTeacher? = null,
    @SerialName("TeacherSecondary")
    val secondTeacher: HebeTeacher? = null,
    @SerialName("Subject")
    val subject: HebeSubject? = null,
    @SerialName("Event")
    val event: String? = null,
    @SerialName("Change")
    val changes: HebeLessonChanges? = null,
    @SerialName("Clazz")
    val teamClass: HebeTeamClass? = null,
    @SerialName("PupilAlias")
    val pupilAlias: String? = null,
    @SerialName("Distribution")
    val group: HebeTeamVirtual? = null,
    @SerialName("Visible")
    val visible: Boolean
)
