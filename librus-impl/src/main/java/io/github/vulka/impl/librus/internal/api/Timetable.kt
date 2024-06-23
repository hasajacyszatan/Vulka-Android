package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.internal.api.types.TimetableResponse

internal suspend fun LibrusUserClient.internalRequestTimetable(weekStart: String) =
    apiGET<TimetableResponse>("Timetables?weekStart=$weekStart").timetable
