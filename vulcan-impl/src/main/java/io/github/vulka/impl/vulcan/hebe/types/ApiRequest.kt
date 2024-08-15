package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.Serializable

@Serializable
data class ApiRequest<T>(
    var appName: String,
    var appVersion: String,
    var certificateId: String,
    var envelope: T,
    var firebaseToken: String,
    var api: Int,
    var requestId: String,
    var timestamp: Long,
    var timestampFormatted: String
)
