package io.github.vulka.core.api

interface Features {
    val isGradesSupported: Boolean
    val isLuckyNumberSupported: Boolean
    val isAttendanceSupported: Boolean
    val isMessagesSupported: Boolean
    val isExamsSupported: Boolean
    val isHomeworkSupported: Boolean
    val isTimetableSupported: Boolean
    val isNotesSupported: Boolean
    val isMeetingsSupported: Boolean
}