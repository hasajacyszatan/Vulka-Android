package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeGrade(
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("PupilId")
    val pupilId: Int,
    @SerialName("ContentRaw")
    val contentRaw: String,
    @SerialName("Content")
    val content: String,
    @SerialName("DateCreated")
    val dateCreated: HebeDate,
    @SerialName("DateModify")
    val dateModified: HebeDate,
    @SerialName("Creator")
    val teacherCreated: HebeTeacher,
    @SerialName("Modifier")
    val teacherModified: HebeTeacher,
    @SerialName("Column")
    val column: HebeGradeColumn,
    @SerialName("Value")
    val value: Float? = null,
    @SerialName("Comment")
    val comment: String? = null,
    @SerialName("Numerator")
    val numerator: Float? = null,
    @SerialName("Denominator")
    val denominator: Float? = null
)

@Serializable
data class HebeGradeColumn(
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("PeriodId")
    val periodId: Int,
    @SerialName("Name")
    val name: String,
    @SerialName("Code")
    val code: String,
    @SerialName("Number")
    val number: Int,
    @SerialName("Weight")
    val weight: Float,
    @SerialName("Subject")
    val subject: HebeSubject,
    @SerialName("Group")
    val group: String? = null,
    @SerialName("Category")
    val category: HebeGradeCategory? = null,
    @SerialName("Period")
    val period: HebePeriod? = null
)

@Serializable
data class HebeGradeCategory(
    @SerialName("Id")
    val id: Int,
    @SerialName("Name")
    val name: String,
    @SerialName("Code")
    val code: String
)
