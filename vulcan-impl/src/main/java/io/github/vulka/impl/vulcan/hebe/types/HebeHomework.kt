package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class HebeHomework(
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("IdPupil")
    val idPupil: Int,
    @SerialName("IdHomework")
    val idHomework: Int,
    @SerialName("Content")
    val content: String,
    @SerialName("IsAnswerRequired")
    val isAnswerRequired: Boolean,
    @SerialName("DateCreated")
    val dateCreated: HebeDate,
    @SerialName("Date")
    val date: HebeDate,
    @SerialName("AnswerDate")
    val answerDate: HebeDate?,
    @SerialName("Deadline")
    val deadline: HebeDate,
    @SerialName("Creator")
    val creator: HebeTeacher,
    @SerialName("Subject")
    val subject: HebeSubject,
    @SerialName("Attachments")
    val attachments: List<HebeAttachment>,
    @SerialName("Didactics")
    @Contextual
    val didactics: JsonElement?,  // Don't know type for this moment
    @SerialName("AnswerDidactics")
    @Contextual
    val answerDidactics: JsonElement?  // Don't know type for this moment
)
