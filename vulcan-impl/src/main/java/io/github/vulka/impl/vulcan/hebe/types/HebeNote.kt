package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class HebeNote(
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("IdPupil")
    val idPupil: Int,
    @SerialName("Positive")
    val positive: Boolean,
    @SerialName("DateValid")
    val dateValid: HebeDate,
    @SerialName("DateModify")
    val dateModify: HebeDate,
    @SerialName("Creator")
    val creator: HebeTeacher,
    @SerialName("Category")
    val category: HebeCategory? = null,
    @SerialName("Content")
    val content: String,
    @SerialName("Points")
    val points: Int
)

@Serializable
data class HebeCategory(
    @SerialName("Id")
    val id: Int,
    @SerialName("Name")
    val name: String? = null,
    @SerialName("Type")
    @Contextual
    val type: JsonElement? = null,
    @SerialName("DefaultPoints")
    @Contextual
    val defaultPoints: JsonElement? = null
)
