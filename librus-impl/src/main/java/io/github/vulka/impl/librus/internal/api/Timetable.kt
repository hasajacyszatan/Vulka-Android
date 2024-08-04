package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

internal suspend fun LibrusUserClient.internalRequestTimetable(weekStart: String) =
    apiGET<LibrusTimetableResponse>("Timetables?weekStart=$weekStart").timetable

@Serializable
internal data class LibrusTimetableResponse(
    @SerialName("Timetable")
    val timetable: JsonObject
)
