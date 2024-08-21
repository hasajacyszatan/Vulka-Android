package io.github.vulka.impl.vulcan.prometheus.hebece.types

import io.github.vulka.impl.vulcan.hebe.types.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeCeChangedLesson(
    @SerialName("Id")
    val id: Int,
    @SerialName("MergeChangeId")
    val mergeChangeId: Int? = null,
    @SerialName("Event")
    val event: String? = null,
    @SerialName("Date")
    val lessonDate: HebeDate,
    @SerialName("Room")
    val room: HebeLessonRoom? = null,
    @SerialName("TimeSlot")
    val time: HebeTimeSlot? = null,
    @SerialName("Subject")
    val subject: HebeSubject? = null,
    @SerialName("TeacherPrimary")
    val teacherPrimary: HebeTeacher? = null,
    @SerialName("TeacherSecondary")
    val secondTeacher: HebeTeacher? = null,
    @SerialName("TeacherSecondary2")
    val thirdTeacher: HebeTeacher? = null,
    @SerialName("Change")
    val changes: HebeLessonChanges? = null,
    @SerialName("Clazz")
    val teamClass: HebeTeamClass? = null,
    @SerialName("Distribution")
    val group: HebeTeamVirtual? = null,
    @SerialName("PupilAlias")
    val pupilAlias: String? = null,
    @SerialName("Visible")
    val visible: Boolean,
    @SerialName("Substitution")
    val substitution: HebeSubstitution? = null,
    @SerialName("Parent")
    val parent: HebeChangedLesson? = null
) {
    @Serializable
    data class HebeSubstitution(
        @SerialName("Id")
        val id: Int,
        @SerialName("UnitId")
        val unitId: Int,
        @SerialName("ScheduleId")
        val scheduleId: Int,
        @SerialName("LessonDate")
        val lessonDate: HebeDate,
        @SerialName("ChangeDate")
        val changeDate: HebeDate? = null,
        @SerialName("Note")
        val note: String? = null,
        @SerialName("Reason")
        val reason: String? = null,
        @SerialName("Event")
        val event: String? = null,
        @SerialName("Room")
        val room: HebeLessonRoom? = null,
        @SerialName("TimeSlot")
        val time: HebeTimeSlot? = null,
        @SerialName("Subject")
        val subject: HebeSubject? = null,
        @SerialName("TeacherPrimary")
        val teacherPrimary: HebeTeacher? = null,
        @SerialName("TeacherAbsenceReasonId")
        val teacherAbsenceReasonId: Int? = null,
        @SerialName("TeacherAbsenceEffectName")
        val teacherAbsenceEffectName: String? = null,
        @SerialName("TeacherSecondary")
        val secondTeacher: HebeTeacher? = null,
        @SerialName("TeacherSecondaryAbsenceReasonId")
        val secondTeacherAbsenceReasonId: Int? = null,
        @SerialName("TeacherSecondaryAbsenceEffectName")
        val secondTeacherAbsenceEffectName: String? = null,
        @SerialName("TeacherSecondary2")
        val thirdTeacher: HebeTeacher? = null,
        @SerialName("TeacherSecondary2AbsenceReasonId")
        val thirdTeacherAbsenceReasonId: Int? = null,
        @SerialName("TeacherSecondary2AbsenceEffectName")
        val thirdTeacherAbsenceEffectName: String? = null,
        @SerialName("Change")
        val changes: HebeLessonChanges? = null,
        @SerialName("Clazz")
        val teamClass: HebeTeamClass? = null,
        @SerialName("Distribution")
        val group: HebeTeamVirtual? = null,
        @SerialName("ClassAbsence")
        val classAbsence: Boolean = false,
        @SerialName("NoRoom")
        val noRoom: Boolean = false,
        @SerialName("DateModified")
        val dateModified: HebeDate,
        @SerialName("Description")
        val description: String? = null
    )
}