package io.github.vulka.impl.librus.api.account

import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.api.getSynergiaApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.getMe() =
    getSynergiaApi<MeResponse>("/Me").me

@Serializable
internal data class MeResponse(
    @SerialName("Me")
    val me: Me
) {
    @Serializable
    data class Me(
        @SerialName("Account")
        val account: MeAccount,
        @SerialName("User")
        val user: MeUser,
        @SerialName("Class")
        val `class`: MeClass
    )

    @Serializable
    data class MeAccount(
        @SerialName("FirstName")
        val firstName: String,
        @SerialName("LastName")
        val lastName: String
        // not all fields are present
    )

    @Serializable
    data class MeUser(
        @SerialName("FirstName")
        val firstName: String,
        @SerialName("LastName")
        val lastName: String
    )

    @Serializable
    data class MeClass(
        @SerialName("Id")
        val id: Int
    )
}
