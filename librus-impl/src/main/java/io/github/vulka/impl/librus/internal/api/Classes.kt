package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.internal.api.types.LibrusIdField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.internalRequestClass(id: Int) =
    apiGET<LibrusClassResponse>("Classes/$id").`class`

@Serializable
internal data class LibrusClassResponse(
    @SerialName("Class")
    val `class`: LibrusClass
) {
    @Serializable
    data class LibrusClass(
        @SerialName("Id")
        val id: Int,
        @SerialName("Number")
        val number: Int,
        @SerialName("Symbol")
        val symbol: String,
        @SerialName("BeginSchoolYear")
        val beginSchoolYear: String,
        @SerialName("EndFirstSemester")
        val endFirstSemester: String,
        @SerialName("EndSchoolYear")
        val endSchoolYear: String,
        @SerialName("ClassTutors")
        val classTutors: List<LibrusIdField>
    )
}
