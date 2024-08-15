package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeSummaryGrade(
    @SerialName("Id")
    val id: Int,
    @SerialName("PupilId")
    val pupilId: Int,
    @SerialName("PeriodId")
    val periodId: Int,
    @SerialName("Subject")
    val subject: HebeSubject,
    @SerialName("Entry_1")
    val entry1: String? = null,
    @SerialName("Entry_2")
    val entry2: String? = null,
    @SerialName("Entry_3")
    val entry3: String? = null,
    @SerialName("DateModify")
    val dateModify: HebeDate
)
