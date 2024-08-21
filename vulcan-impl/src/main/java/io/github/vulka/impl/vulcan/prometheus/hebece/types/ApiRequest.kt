package io.github.vulka.impl.vulcan.prometheus.hebece.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiRequest<T>(
    @SerialName("AppName")
    var appName: String,
    @SerialName("AppVersion")
    var appVersion: String,
    @SerialName("Envelope")
    var envelope: T,
    @SerialName("NotificationToken")
    var firebaseToken: String,
    @SerialName("API")
    var api: Int,
    @SerialName("RequestId")
    var requestId: String,
    @SerialName("Timestamp")
    var timestamp: Long,
    @SerialName("TimestampFormatted")
    var timestampFormatted: String
)
