package io.github.vulka.impl.vulcan.hebe.types

import com.google.gson.annotations.SerializedName

data class HebeAverageGrade(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("PupilId")
    val pupilId: Int,
    @SerializedName("PeriodId")
    val periodId: Int,
    @SerializedName("Subject")
    val subject: HebeSubject,
    @SerializedName("Average")
    val average: String?,
    @SerializedName("Points")
    val points: Any?,
    @SerializedName("Scope")
    val scope: String
)