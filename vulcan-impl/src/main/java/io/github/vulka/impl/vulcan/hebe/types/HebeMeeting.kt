package io.github.vulka.impl.vulcan.hebe.types

import com.google.gson.annotations.SerializedName

data class HebeMeeting(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("When")
    val `when`: HebeDate,
    @SerializedName("Where")
    val where: String,
    @SerializedName("Why")
    val why: String,
    @SerializedName("Agenda")
    val agenda: String,
    @SerializedName("AdditionalInfo")
    val additionalInfo: Any,
    @SerializedName("Online")
    val online: Any,
)