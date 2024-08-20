package io.github.vulka.core.api.types

import java.time.LocalDate
import java.time.LocalTime

enum class PresenceType {
    Presence,
    Absence,
    AbsenceExcused,
    AbsenceForSchoolReasons,
    Exemption,
    Lateness,
    LatenessExcused,
    Unknown,
}

enum class JustificationStatus {
    Requested,
    Accepted,
    Rejected
}

data class Attendance(
    val subject: String,
    val date: LocalDate,
    val time: LocalTime?,
    val teacher: String,
    val presenceType: PresenceType,
    val justificationStatus: JustificationStatus?,
    // If your platform supports getting lesson topic from realised lesson, you can set it,
    // Otherwise set null
    val topic: String?,
    // If your platform supports it, you can set lesson position in timetable, to properly order it in attendance view.
    // This property is strongly recommend to be set, if your platform don't support position directly, try to generate
    // it yourself from lesson hours, set to null in the last resort.
    val position: Int?
)