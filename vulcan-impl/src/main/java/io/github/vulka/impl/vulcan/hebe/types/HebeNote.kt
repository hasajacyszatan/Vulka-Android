package io.github.vulka.impl.vulcan.hebe.types

import com.google.gson.annotations.SerializedName

data class HebeNote(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Key")
    val key: String,
    @SerializedName("IdPupil")
    val idPupil: Int,
    @SerializedName("Positive")
    val positive: Boolean,
    @SerializedName("DateValid")
    val dateValid: HebeDate,
    @SerializedName("DateModify")
    val dateModify: HebeDate,
    @SerializedName("Creator")
    val creator: HebeTeacher,
    @SerializedName("Category")
    val category: HebeCategory,
    @SerializedName("Content")
    val content: String,
    // Don't know type for this moment
    @SerializedName("Points")
    val points: String
)

data class HebeCategory(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Type")
    val type: Any,
    @SerializedName("DefaultPoints")
    val defaultPoints: Any
)