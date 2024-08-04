package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.internalRequestMe() =
    apiGET<LibrusMeResponse>("Me").me

@Serializable
internal data class LibrusMeResponse(
    @SerialName("Me")
    val me: LibrusMe
)

@Serializable
internal data class LibrusMe(
    @SerialName("Account")
    val account: LibrusMeAccount,
    @SerialName("User")
    val user: LibrusMeUser,
    @SerialName("Class")
    val `class`: LibrusMeClass
)

@Serializable
internal data class LibrusMeAccount(
    @SerialName("FirstName")
    val firstName: String,
    @SerialName("LastName")
    val lastName: String
)

@Serializable
internal data class LibrusMeUser(
    @SerialName("FirstName")
    val firstName: String,
    @SerialName("LastName")
    val lastName: String
)

@Serializable
internal data class LibrusMeClass(
    @SerialName("Id")
    val id: Int
)
