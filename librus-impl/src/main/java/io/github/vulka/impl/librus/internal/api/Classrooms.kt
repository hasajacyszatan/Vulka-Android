package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.internalRequestClassrooms() =
    apiGET<LibrusClassroomsResponse>("Classrooms").classrooms

@Serializable
internal data class LibrusClassroomsResponse(
    @SerialName("Class")
    val classrooms: List<Classroom>
) {
    @Serializable
    data class Classroom(
        @SerialName("Id")
        val id: String,
        @SerialName("Name")
        val name: String,
        @SerialName("Symbol")
        val symbol: String
    )
}
