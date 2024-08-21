package io.github.vulka.impl.librus.api.account

import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.librus.api.getSynergiaApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.getUserProfile() =
    getSynergiaApi<UserProfileResponse>("/UserProfile").userProfile

@Serializable
internal data class UserProfileResponse(
    @SerialName("UserProfile")
    val userProfile: UserProfile
) {
    @Serializable
    data class UserProfile(
        @SerialName("AccountType")
        val accountType: AccountType
    )

    @Serializable
    enum class AccountType {
        @SerialName("parent")
        Parent,
        @SerialName("student")
        Student
    }
}
