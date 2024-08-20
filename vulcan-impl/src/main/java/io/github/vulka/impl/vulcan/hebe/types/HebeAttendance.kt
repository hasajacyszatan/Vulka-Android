package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class HebeAttendance(
    @SerialName("LessonId")
    val lessonId: Int,
    @SerialName("PresenceType")
    val presenceType: HebePresenceType,
    @SerialName("Collection")
    // Don't know type for this moment
    val collection: JsonElement,
    @SerialName("JustificationStatus")
    val justificationStatus: Int? = null,
    @SerialName("Id")
    val id: Int,
    @SerialName("LessonClassId")
    val lessonClassId: Int,
    @SerialName("Day")
    val day: HebeDate,
    @SerialName("CalculatePresence")
    val calculatePresence: Boolean,
    @SerialName("GroupDefinition")
    val groupDefinition: String,
    @SerialName("PublicResources")
    val publicResources: String? = null,
    @SerialName("RemoteResources")
    // Don't know type for this moment
    val remoteResources: JsonElement? = null,
    @SerialName("Replacement")
    val replacement: Boolean,
    @SerialName("DateModify")
    val dateModify: HebeDate,
    @SerialName("GlobalKey")
    val globalKey: String,
    @SerialName("Note")
    val note: String? = null,
    @SerialName("Topic")
    val topic: String,
    @SerialName("LessonNumber")
    val lessonNumber: Int,
    @SerialName("LessonClassGlobalKey")
    val lessonClassGlobalKey: String,
    @SerialName("TimeSlot")
    val timeSlot: HebeTimeSlot,
    @SerialName("Subject")
    val subject: HebeSubject,
    @SerialName("TeacherPrimary")
    val teacherPrimary: HebeTeacher,
    @SerialName("TeacherSecondary")
    val teacherSecondary: HebeTeacher? = null,
    @SerialName("TeacherMod")
    val teacherMod: HebeTeacher,
    @SerialName("Clazz")
    val clazz: HebeTeamClass,
    @SerialName("Distribution")
    val distribution: HebeTeamVirtual? = null,
    @SerialName("Visible")
    val visible: Boolean,
    @SerialName("Didactics")
    // Don't know type for this moment
    val didactics: JsonElement? = null
)

@Serializable
data class HebePresenceType(
    @SerialName("Id")
    val id: Int,
    @SerialName("Symbol")
    val symbol: String,
    @SerialName("Name")
    val name: String,
    @SerialName("CategoryId")
    val categoryId: Int,
    @SerialName("CategoryName")
    val categoryName: String,
    @SerialName("Position")
    val position: Int,
    @SerialName("Presence")
    val presence: Boolean,
    @SerialName("Absence")
    val absence: Boolean,
    @SerialName("LegalAbsence")
    val legalAbsence: Boolean,
    @SerialName("Late")
    val late: Boolean,
    @SerialName("AbsenceJustified")
    val absenceJustified: Boolean,
    @SerialName("Removed")
    val removed: Boolean
)