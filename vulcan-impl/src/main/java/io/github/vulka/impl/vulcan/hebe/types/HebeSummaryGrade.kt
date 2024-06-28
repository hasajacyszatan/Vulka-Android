package io.github.vulka.impl.vulcan.hebe.types

import com.google.gson.annotations.SerializedName


data class HebeSummaryGrade(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("PupilId")
    val pupilId: Int,
    @SerializedName("PeriodId")
    val periodId: Int,
    @SerializedName("Subject")
    val subject: HebeSubject,
    @SerializedName("Entry_1")
    val entry1: String?,
    @SerializedName("Entry_2")
    val entry2: String?,
    @SerializedName("Entry_3")
    val entry3: String?,
    @SerializedName("DateModify")
    val dateModify: HebeTimeSlot
)