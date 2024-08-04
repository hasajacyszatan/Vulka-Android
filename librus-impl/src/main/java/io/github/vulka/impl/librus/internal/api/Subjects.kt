package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.internalRequestSubjects() =
    apiGET<LibrusSubjectsResponse>("Subjects").subjects

@Serializable
internal data class LibrusSubjectsResponse(
    @SerialName("Subjects")
    val subjects: List<Subject>
) {
    @Serializable
    data class Subject(
        @SerialName("Id")
        val id: String,
        @SerialName("Name")
        val name: String
    )
}
