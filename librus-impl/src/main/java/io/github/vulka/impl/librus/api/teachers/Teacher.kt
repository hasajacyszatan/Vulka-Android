package io.github.vulka.impl.librus.api.teachers

import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.api.getSynergiaApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.getTeachers(endpoint: String) =
    getSynergiaApi<UsersResponse>("/Users").users

@Serializable
internal data class UsersResponse(
    @SerialName("Users")
    val users: List<User>
) {
    @Serializable
    data class User(
        @SerialName("Id")
        val id: Int,
        @SerialName("FirstName")
        val firstName: String,
        @SerialName("LastName")
        val lastName: String
    )
}
