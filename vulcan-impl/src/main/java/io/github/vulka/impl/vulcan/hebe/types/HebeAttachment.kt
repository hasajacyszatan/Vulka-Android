package io.github.vulka.impl.vulcan.hebe.types

import com.google.gson.annotations.SerializedName

data class HebeAttachment(
    @SerializedName("Name")
    val name: String,
    @SerializedName("Link")
    val link: String
)