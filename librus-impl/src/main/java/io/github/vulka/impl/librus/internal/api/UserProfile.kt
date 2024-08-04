package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.internalRequestUserProfile() =
    apiGET<LibrusUserProfileResponse>("UserProfile").userProfile

@Serializable
internal data class LibrusUserProfileResponse(
    @SerialName("UserProfile")
    val userProfile: UserProfile
) {
    @Serializable
    internal data class UserProfile(
        @SerialName("AccountType")
        val accountType: AccountType
    )

    @Serializable
    internal enum class AccountType {
        @SerialName("parent")
        Parent,
        @SerialName("student")
        Student
    }
}
