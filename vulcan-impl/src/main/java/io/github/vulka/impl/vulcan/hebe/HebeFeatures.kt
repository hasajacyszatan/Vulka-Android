package io.github.vulka.impl.vulcan.hebe

import io.github.vulka.core.api.Features

class HebeFeatures : Features {
    override val isGradesSupported: Boolean = true
    override val isLuckyNumberSupported: Boolean = true
    override val isAttendanceSupported: Boolean = true
    override val isMessagesSupported: Boolean = true
    override val isExamsSupported: Boolean = true
    override val isHomeworkSupported: Boolean = true
    override val isTimetableSupported: Boolean = true
    override val isNotesSupported: Boolean = true
    override val isMeetingsSupported: Boolean = true

    companion object {
        @JvmStatic
        fun get() = HebeFeatures()
    }
}