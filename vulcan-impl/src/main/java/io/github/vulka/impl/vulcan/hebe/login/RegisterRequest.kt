package io.github.vulka.impl.vulcan.hebe.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerialName("OS")
    val os: String,
    @SerialName("deviceModel")
    val deviceModel: String,
    @SerialName("certificate")
    val certificate: String,
    @SerialName("certificateType")
    val certificateType: String,
    @SerialName("certificateThumbprint")
    val certificateThumbprint: String,
    @SerialName("pin")
    val pin: String,
    @SerialName("securityToken")
    val securityToken: String,
    @SerialName("selfIdentifier")
    val selfIdentifier: String
)
