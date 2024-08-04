package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.internal.api.types.LibrusIdField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.internalRequestGrades() =
    apiGET<LibrusGradesResponse>("Grades").grades

internal suspend fun LibrusUserClient.internalRequestGradesCategories() =
    apiGET<LibrusGradesCategoriesResponse>("Grades/Categories").categories

@Serializable
internal data class LibrusGradesResponse(
    @SerialName("Grades")
    val grades: List<Grade>
) {
    @Serializable
    data class Grade(
        @SerialName("Id")
        val id: Int,
        @SerialName("Grade")
        val grade: String,
        @SerialName("Lesson")
        val lesson: LibrusIdField,
        @SerialName("AddedBy")
        val addedBy: LibrusIdField,
        @SerialName("Subject")
        val subject: LibrusIdField,
        @SerialName("Category")
        val category: LibrusIdField,
        @SerialName("Date")
        val date: String,
        @SerialName("AddDate")
        val addDate: String
    )
}

@Serializable
internal data class LibrusGradesCategoriesResponse(
    @SerialName("Categories")
    val categories: List<Category>
) {
    @Serializable
    data class Category(
        @SerialName("Id")
        val id: String,
        @SerialName("Name")
        val name: String?,
        @SerialName("Weight")
        val weight: Float?,
        @SerialName("IsFinal")
        val final: Boolean,
        @SerialName("IsFinalProposition")
        val finalProposition: Boolean,
        @SerialName("IsSemestral")
        val semestral: Boolean,
        @SerialName("IsSemestralProposition")
        val semestralProposition: Boolean,
        @SerialName("Teacher")
        val teacher: LibrusIdField
    )
}
