package io.github.vulka.impl.librus

import io.github.vulka.core.api.Features

class LibrusFeatures : Features {
    override val isGradesSupported: Boolean = true
    override val isLuckyNumberSupported: Boolean = true
    override val isAttendanceSupported: Boolean = true
    override val isMessagesSupported: Boolean = true
    override val isExamsSupported: Boolean = true
    override val isHomeworkSupported: Boolean = true
    override val isTimetableSupported: Boolean = true
    override val isNotesSupported: Boolean = true
    // Meetings are not supported in Librus
    override val isMeetingsSupported: Boolean = false

    companion object {
        @JvmStatic
        fun get() = LibrusFeatures()
    }
}