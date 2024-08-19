package io.github.vulka.impl.vulcan.prometheus.hebece.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RegisterRequest(
    @SerialName("OS")
    val os: String,
    @SerialName("DeviceModel")
    val deviceModel: String,
    @SerialName("Certificate")
    val certificate: String,
    @SerialName("CertificateType")
    val certificateType: String,
    @SerialName("CertificateThumbprint")
    val certificateThumbprint: String,
    @SerialName("Tokens")
    val tokens: Array<String>,
    @SerialName("selfIdentifier")
    val selfIdentifier: String
)