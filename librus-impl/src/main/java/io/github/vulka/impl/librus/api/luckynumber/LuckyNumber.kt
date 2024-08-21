package io.github.vulka.impl.librus.api.luckynumber

import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.api.common.IdField
import io.github.vulka.impl.librus.api.getSynergiaApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.getClass() =
    getSynergiaApi<ClassResponse>("/LuckyNumbers").`class`

@Serializable
internal data class ClassResponse(
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
        @SerialName("ClassTutor")
        val classTutor: IdField,
//        @SerialName("ClassTutors")
//        val classTutors: List<IdField>
    )
}