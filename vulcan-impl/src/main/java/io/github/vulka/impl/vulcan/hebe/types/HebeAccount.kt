package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeAccount(
    @SerialName("LoginId")
    val loginId: Int? = null,
    @SerialName("RestURL")
    val restUrl: String,
    @SerialName("UserLogin")
    val userLogin: String? = null,
    @SerialName("UserName")
    val userName: String? = null
)