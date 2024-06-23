package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.internal.api.types.ClassroomsResponse

internal suspend fun LibrusUserClient.internalRequestClassrooms() =
    apiGET<ClassroomsResponse>("Classrooms").classrooms
