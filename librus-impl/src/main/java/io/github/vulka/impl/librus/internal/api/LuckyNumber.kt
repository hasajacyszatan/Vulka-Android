package io.github.vulka.impl.librus.internal.api

import io.github.vulka.impl.librus.LibrusUserClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal suspend fun LibrusUserClient.internalRequestLuckyNumber() =
    apiGET<LibrusLuckyNumberResponse>("LuckyNumbers").luckyNumber

@Serializable
internal data class LibrusLuckyNumberResponse(
    @SerialName("LuckyNumber")
    val luckyNumber: LuckyNumber
)

@Serializable
internal data class LuckyNumber(
    @SerialName("LuckyNumber")
    val number: Int?
//    @SerialName("LuckyNumberDay")
//    val day: String?
)
