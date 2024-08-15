package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeChangedLesson(
    @SerialName("Id")
    val id: Int,
    @SerialName("UnitId")
    val unitId: Int,
    @SerialName("ScheduleId")
    val scheduleId: Int,
    @SerialName("LessonDate")
    val lessonDate: HebeDate,
    @SerialName("Note")
    val note: String? = null,
    @SerialName("Reason")
    val reason: String? = null,
    @SerialName("TimeSlot")
    val time: HebeTimeSlot? = null,
    @SerialName("Room")
    val room: HebeLessonRoom? = null,
    @SerialName("TeacherPrimary")
    val teacherPrimary: HebeTeacher? = null,
    @SerialName("TeacherSecondary")
    val secondTeacher: HebeTeacher? = null,
    @SerialName("TeacherAbsenceEffectName")
    val teacherAbsenceEffectName: String?,
    @SerialName("Subject")
    val subject: HebeSubject? = null,
    @SerialName("Event")
    val event: String? = null,
    @SerialName("Change")
    val changes: HebeLessonChanges? = null,
    @SerialName("ChangeDate")
    val changeDate: HebeDate? = null,
    @SerialName("Clazz")
    val teamClass: HebeTeamClass? = null,
    @SerialName("Distribution")
    val group: HebeTeamVirtual? = null
)
