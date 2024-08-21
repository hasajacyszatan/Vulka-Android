package io.github.vulka.impl.vulcan.prometheus.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiApResponse(
    @SerialName("Success")
    val success: Boolean,
    @SerialName("Tokens")
    val tokens: List<String>,
    @SerialName("Alias")
    val alias: String,
    @SerialName("Email")
    val email: String?,
    @SerialName("GivenName")
    val givenName: String?,
    @SerialName("Surname")
    val surname: String?,
    @SerialName("IsConsentAccepted")
    val isConsentAccepted: Boolean,
    @SerialName("CanAcceptConsent")
    val canAcceptConsent: Boolean,
    @SerialName("AccessToken")
    val accessToken: String,
    @SerialName("ErrorMessage")
    val errorMessage: String?
)
