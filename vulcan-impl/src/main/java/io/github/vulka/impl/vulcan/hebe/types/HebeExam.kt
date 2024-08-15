package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class HebeExam(
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("Type")
    val type: String,
    @SerialName("TypeId")
    val typeId: Int,
    @SerialName("Content")
    val content: String,
    @SerialName("DateCreated")
    val dateCreated: HebeDate,
    @SerialName("DateModify")
    val dateModify: HebeDate,
    @SerialName("Deadline")
    val deadline: HebeDate,
    @SerialName("Creator")
    val creator: HebeTeacher,
    @SerialName("Subject")
    val subject: HebeSubject,
    @SerialName("Didactics")
    @Contextual
    val didactics: JsonElement?
)
