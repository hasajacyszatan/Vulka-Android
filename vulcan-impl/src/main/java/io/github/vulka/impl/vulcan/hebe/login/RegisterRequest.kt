package io.github.vulka.impl.vulcan.hebe.login

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("OS")
    val os: String,
    val deviceModel: String,
    val certificate: String,
    val certificateType: String,
    val certificateThumbprint: String,
    val pin: String,
    val securityToken: String,
    val selfIdentifier: String
)
