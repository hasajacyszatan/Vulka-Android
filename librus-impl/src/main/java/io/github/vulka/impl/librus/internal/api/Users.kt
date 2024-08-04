package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.internalRequestUsers() =
    apiGET<LibrusUsersResponse>("Users").users

@Serializable
internal data class LibrusUsersResponse(
    @SerialName("Users")
    val users: List<User>
) {
    @Serializable
    data class User(
        @SerialName("Id")
        val id: String,
        @SerialName("FirstName")
        val firstName: String,
        @SerialName("LastName")
        val lastName: String
    )
}
