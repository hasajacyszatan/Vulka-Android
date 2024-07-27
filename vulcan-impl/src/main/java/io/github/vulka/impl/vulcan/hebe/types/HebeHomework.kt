package io.github.vulka.impl.vulcan.hebe.types

import com.google.gson.annotations.SerializedName

data class HebeHomework(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Key")
    val key: String,
    @SerializedName("IdPupil")
    val idPupil: Int,
    @SerializedName("IdHomework")
    val idHomework: Int,
    @SerializedName("Content")
    val content: String,
    @SerializedName("IsAnswerRequired")
    val isAnswerRequired: Boolean,
    @SerializedName("DateCreated")
    val dateCreated: HebeDate,
    @SerializedName("Date")
    val date: HebeDate,
    @SerializedName("AnswerDate")
    val answerDate: HebeDate?,
    @SerializedName("Deadline")
    val deadline: HebeDate,
    @SerializedName("Creator")
    val creator: HebeTeacher,
    @SerializedName("Subject")
    val subject: HebeSubject,
    @SerializedName("Attachments")
    val attachments: List<HebeAttachment>,
    @SerializedName("Didactics")
    val didactics: Any?,
    @SerializedName("AnswerDidactics")
    val answerDidactics: Any?
)