package io.github.vulka.impl.librus.internal.api.types

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class TimetableResponse(
    @SerializedName("Timetable")
    val timetable: JsonObject
)
